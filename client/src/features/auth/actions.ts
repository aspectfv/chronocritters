import { redirect } from 'react-router-dom';
import { useAuthStore } from '@features/auth/store/useAuthStore';
import { login as apiLogin, register as apiRegister } from '@api/user';
import type { LoginCredentials, RegisterCredentials } from '@features/auth/types';

export async function loginAction({ request }: { request: Request }) {
  const formData = await request.formData();
  
  const credentials: LoginCredentials = {
    username: formData.get('username') as string,
    password: formData.get('password') as string,
  };

  // Basic validation
  if (!credentials.username || !credentials.password) {
    return {
      message: 'Username and password are required',
      field: !credentials.username ? 'username' : 'password'
    };
  }

  try {
    // Call actual login API
    const response = await apiLogin(credentials);
    const { user, token } = response.data;

    // Get the auth store and login
    const { login } = useAuthStore.getState();
    login(user, token);

    // Redirect to menu page
    return redirect('/menu');
  } catch (error) {
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

  // Basic validation
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
    // Call actual register API
    const response = await apiRegister(credentials);
    const { user, token } = response.data;

    // Get the auth store and login the user after registration
    const { login } = useAuthStore.getState();
    login(user, token);

    // Redirect to menu page
    return redirect('/menu');
  } catch (error) {
    return {
      message: 'Registration failed. Please try again.',
      field: null
    };
  }
}
