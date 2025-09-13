import { redirect } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { login as apiLogin, register as apiRegister } from '@api/user';
import type { LoginCredentials, RegisterCredentials } from '@store/auth/types';
import { CombinedGraphQLErrors } from "@apollo/client/errors";

export async function loginAction({ request }: { request: Request }) {
  const formData = await request.formData();
  
  const credentials: LoginCredentials = {
    username: formData.get('username') as string,
    password: formData.get('password') as string,
  };

  if (!credentials.username || !credentials.password) {
    return {
      message: 'Username and password are required',
      field: !credentials.username ? 'username' : 'password'
    };
  }

  try {
    const response = await apiLogin(credentials);
    if (!response.data) {
      throw new Error('Login failed: No data returned.');
    }

    const loginData = response.data.login;
    if (!loginData) throw new Error('Login failed: Invalid credentials.');
    const { user, token } = loginData;

    const { login } = useAuthStore.getState();
    login(user, token);

    return redirect('/menu');
  } catch (error) {
    if (CombinedGraphQLErrors.is(error)) {
      console.error("GraphQL Login Error:", error.errors);
    }
    return {
      message: 'Login failed. Please check your credentials.',
      field: null
    };
  }
}

export async function registerAction({ request }: { request: Request }) {
  const formData = await request.formData();
  
  const credentials: RegisterCredentials = {
    username: formData.get('username') as string,
    password: formData.get('password') as string,
    confirmPassword: formData.get('confirmPassword') as string,
  };

  if (!credentials.username || !credentials.password || !credentials.confirmPassword) {
    return {
      message: 'All fields are required',
      field: !credentials.username ? 'username' : 
             !credentials.password ? 'password' : 'confirmPassword'
    };
  }

  if (credentials.password !== credentials.confirmPassword) {
    return {
      message: 'Passwords do not match',
      field: 'confirmPassword'
    };
  }

  if (credentials.password.length < 6) {
    return {
      message: 'Password must be at least 6 characters long',
      field: 'password'
    };
  }

  try {
    const response = await apiRegister(credentials);
    if (!response.data) {
        throw new Error('Registration failed: No data returned.');
    }
    const registerData = response.data.register;
    if (!registerData) throw new Error('Registration failed: Invalid data returned.');
    const { user, token } = registerData;
    
    const { login } = useAuthStore.getState();
    login(user, token);

    return redirect('/menu');
  } catch (error) {
    if (CombinedGraphQLErrors.is(error)) {
      if (error.errors && error.errors.some(gqlError => gqlError.message.includes("Username already exists"))) {
        return {
          message: 'Username already exists. Please choose another one.',
          field: 'username'
        };
      }
    }

    return {
      message: 'Registration failed. Please try again.',
      field: null
    };
  }
}