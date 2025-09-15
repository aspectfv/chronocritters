import { redirect } from 'react-router-dom';
import { getBattleState } from '@api/gamelogic';
import type { BattleLoaderParams } from './types';

export async function battleLoader({ params }: BattleLoaderParams) {
  const { battleId } = params;

  if (!battleId) {
    console.error("No battle ID provided to loader, redirecting.");
    throw redirect('/menu');
  }

  try {
    const response = await getBattleState(battleId);
    return response.data;
  } catch (error) {
    console.error(`Failed to load battle state for battle ${battleId}:`, error);
    throw redirect('/menu');
  }
}