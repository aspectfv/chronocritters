import { redirect } from 'react-router-dom';
import { useAuthStore } from '@features/auth/store/useAuthStore';
import type { LoginCredentials, RegisterCredentials, User } from '@features/auth/types';

// Simulate API calls - replace with real API calls
const simulateApiCall = (delay: number = 1000) => 
  new Promise(resolve => setTimeout(resolve, delay));

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
    // Simulate API call
    await simulateApiCall(500);

    // Simulate successful login - replace with real API call
    const user: User = {
      id: '1',
      username: credentials.username,
    };

    // Get the auth store and login
    const { login } = useAuthStore.getState();
    login(user, 'mock-token-123');

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
    // Simulate API call
    await simulateApiCall(1000);

    // Simulate successful registration - replace with real API call
    const user: User = {
      id: '1',
      username: credentials.username,
    } as User;

    // Get the auth store and login the user after registration
    const { login } = useAuthStore.getState();
    login(user, 'mock-token-123');

    // Redirect to menu page
    return redirect('/menu');
  } catch (error) {
    return {
      error: 'Registration failed. Please try again.',
      field: null
    };
  }
}
