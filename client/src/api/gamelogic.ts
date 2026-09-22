import axios from 'axios';
import { applyAuthTokenInterceptor } from '@api/interceptors';
import type { BattleData } from '@store/battle/types';

const gamelogicClient = axios.create({
  baseURL: import.meta.env.VITE_GAME_LOGIC_SERVICE_URL ?? '',
});

applyAuthTokenInterceptor(gamelogicClient);

// The acting player is taken from the JWT server-side, so it is never sent here.
export const getBattleState = (battleId: string) => gamelogicClient.get<BattleData>(`/battle/${battleId}`);
export const executeAbility = (battleId: string, abilityId: string) => gamelogicClient.post<BattleData>(`/battle/${battleId}/ability`, { abilityId });
export const switchCritter = (battleId: string, targetCritterIndex: number) => gamelogicClient.post<BattleData>(`/battle/${battleId}/switch`, { targetCritterIndex });
