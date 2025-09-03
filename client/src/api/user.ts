import axios from 'axios';
import { applyAuthTokenInterceptor } from '@api/interceptors';
import type { LoginCredentials, LoginResponse, RegisterCredentials } from '@store/auth/types';

const userClient = axios.create({
  baseURL: import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8080',
});

// Add the auth token logic to this client
applyAuthTokenInterceptor(userClient);

export const login = (credentials: LoginCredentials) => userClient.post<LoginResponse>('/auth/login', credentials);
export const register = (credentials: RegisterCredentials) => userClient.post<LoginResponse>('/auth/register', credentials);