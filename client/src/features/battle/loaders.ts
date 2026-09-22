import { redirect } from 'react-router-dom';
import { getBattleState } from '@api/gamelogic';
import type { BattleLoaderParams } from './types';

export async function battleLoader({ params }: BattleLoaderParams) {
  const { battleId } = params;

  if (!battleId) {
    throw redirect('/menu');
  }

  try {
    const response = await getBattleState(battleId);
    return response.data;
  } catch (error) {
    // Battles are held in memory, so a restart or the cleanup sweep can take one
    // away while a link to it is still live. The menu says so rather than
    // bouncing the player back with no explanation.
    console.error(`Failed to load battle state for battle ${battleId}:`, error);
    throw redirect('/menu?notice=battle-ended');
  }
}
