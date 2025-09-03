import axios from 'axios';
import { applyAuthTokenInterceptor } from '@api/interceptors';

export const userClient = axios.create({
  baseURL: import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8081', // User service port
});

export const lobbyClient = axios.create({
  baseURL: import.meta.env.VITE_LOBBY_SERVICE_URL || 'http://localhost:8082', // Lobby service port
});

export const gameLogicClient = axios.create({
  baseURL: import.meta.env.VITE_GAME_LOGIC_SERVICE_URL || 'http://localhost:8083', // Game Logic service port
});

// Add the auth token logic to this client
applyAuthTokenInterceptor(userClient);
applyAuthTokenInterceptor(lobbyClient);
applyAuthTokenInterceptor(gameLogicClient);