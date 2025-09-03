import axios from 'axios';
import { applyAuthTokenInterceptor } from '@api/interceptors';
import type { LoginCredentials, RegisterCredentials } from '@features/auth/types';

export const userClient = axios.create({
  baseURL: import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8081', // User service port
});

// Add the auth token logic to this client
applyAuthTokenInterceptor(userClient);

export const login = (credentials: LoginCredentials) => userClient.post('/login', credentials);
export const register = (credentials: RegisterCredentials) => userClient.post('/register', credentials);