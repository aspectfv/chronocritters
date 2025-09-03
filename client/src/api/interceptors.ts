import type { AxiosInstance } from 'axios';
import { useAuthStore } from '@store/auth/useAuthStore';

export const applyAuthTokenInterceptor = (apiClient: AxiosInstance) => {
  apiClient.interceptors.request.use(
    (config) => {
      const token = useAuthStore.getState().token;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
};