import axios from 'axios';
import { applyAuthTokenInterceptor } from '@api/interceptors';
import type { BattleState } from '@store/battle/types';

const gamelogicClient = axios.create({
  baseURL: import.meta.env.VITE_GAME_LOGIC_SERVICE_URL || 'http://localhost:8082',
});

applyAuthTokenInterceptor(gamelogicClient);

export const getBattleState = (battleId: string) => gamelogicClient.get<BattleState>(`/battle/${battleId}`);
export const executeAbility = (battleId: string, playerId: string, abilityId: string) => gamelogicClient.post<BattleState>(`/battle/${battleId}/ability`, { playerId, abilityId });
export const switchCritter = (battleId: string, playerId: string, targetCritterIndex: number) => gamelogicClient.post<BattleState>(`/battle/${battleId}/switch`, { playerId, targetCritterIndex });