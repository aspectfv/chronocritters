import { useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
import type { BattleState } from '@store/battle/types';

import { BattleHeader } from '@features/battle/components/BattleHeader';
import { TimerBar } from '@features/battle/components/TimerBar';
import { CritterDisplayCard } from '@features/battle/components/CritterDisplayCard';
import { TeamDisplay } from '@features/battle/components/TeamDisplay';
import { BattleLog } from '@features/battle/components/BattleLog';
import { AbilitySelector } from '@features/battle/components/AbilitySelector';
import { getBattleState, executeAbility, switchCritter } from '@api/gamelogic';
import { ConnectionStatus } from '@store/lobby/types';

function BattlePage() {
  const { battleId } = useParams<{ battleId: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);

  const isConnected = useLobbyStore((state) => state.connectionStatus === ConnectionStatus.CONNECTED);
  const { player, opponent, actionLogHistory, timeRemaining, battleResult } = useBattleStore();

  useEffect(() => {
    const { setBattleState } = useBattleStore.getState();

    if (!battleId || !user?.id) return;

    const fetchBattleState = async () => {
      try {
        const response = await getBattleState(battleId);
        setBattleState(response.data, user.id);
      } catch (error) {
        console.error("Failed to fetch initial battle state:", error);
        navigate('/menu');
      }
    };

    fetchBattleState();
  }, [battleId, user?.id, navigate]);

  useEffect(() => {
    const { subscribe } = useLobbyStore.getState();
    const { setBattleState } = useBattleStore.getState();

    if (!isConnected || !battleId || !user?.id) return;

    const subscription = subscribe(`/topic/battle/${battleId}`, (newBattleState: BattleState) => {
      setBattleState(newBattleState, user.id);
    });

    return () => {
      subscription?.unsubscribe();
    };
  }, [isConnected, battleId, user?.id]);

  useEffect(() => {
    if (opponent.roster.length > 0 && opponent.roster.every(critter => critter.stats.currentHp <= 0)) {
      navigate(`/results/${battleId}`, { state: { result: 'victory' } });
    } else if (player.roster.length > 0 && player.roster.every(critter => critter.stats.currentHp <= 0)) {
      navigate(`/results/${battleId}`, { state: { result: 'defeat' } });
    }
  }, [player, opponent, navigate, battleId]);

  useEffect(() => {
    if (battleResult) {
      navigate(`/results/${battleId}`);
    }
  }, [battleResult, navigate]);

  const handleAbilityClick = useCallback(async (abilityId: string) => {
    const { player } = useBattleStore.getState();

    if (!player.hasTurn || !battleId || !user?.id) {
      return;
    }
    
    try {
      await executeAbility(battleId, user.id, abilityId);
    } catch (error) {
      console.error("Failed to execute ability:", error);
    }
  }, [battleId, user?.id]);

  const handleSwitchCritter = useCallback(async (targetCritterIndex: number) => {
    if (!player.hasTurn || !battleId || !user?.id) {
      return;
    }

    try {
      await switchCritter(battleId, user.id, targetCritterIndex);
    } catch (error) {
      console.error("Failed to execute switch:", error);
    }
  }, [battleId, user?.id, player.hasTurn]);
  
  return (
    <div className="min-h-screen bg-[#F8FFF8] p-4 sm:p-6 md:p-8">
      <div className="max-w-4xl mx-auto">
        <BattleHeader isPlayerTurn={player.hasTurn} />
        <TimerBar timeRemaining={timeRemaining} />

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 my-6">
          <CritterDisplayCard playerName={player.username} critter={player.activeCritter} />
          <CritterDisplayCard playerName={opponent.username} critter={opponent.activeCritter} />
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6 mb-12">
          <TeamDisplay title="Your Team" team={player.roster} activeCritterId={player.activeCritter.id} isPlayerTurn={player.hasTurn} onCritterClick={handleSwitchCritter} />
          <TeamDisplay title="Opponent's Team" team={opponent.roster} activeCritterId={opponent.activeCritter.id} isPlayerTurn={false} onCritterClick={() => {}} />
        </div>

        <BattleLog log={actionLogHistory} />
        
        <AbilitySelector 
          abilities={player.activeCritter.abilities} 
          onAbilityClick={handleAbilityClick}
          isPlayerTurn={player.hasTurn} 
          critterType={player.activeCritter.type}
        />
      </div>
    </div>
  );
}

export default BattlePage;