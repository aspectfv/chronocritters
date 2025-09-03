import axios from 'axios';
import { applyAuthTokenInterceptor } from '@api/interceptors';
import type { BattleState, executeAbilityRequest } from '@store/battle/types';

const gamelogicClient = axios.create({
  baseURL: import.meta.env.VITE_GAME_LOGIC_SERVICE_URL || 'http://localhost:8082',
});

applyAuthTokenInterceptor(gamelogicClient);

export const getBattleState = (request: executeAbilityRequest) => gamelogicClient.post<BattleState>(`/battle/${request.battleId}`, request);