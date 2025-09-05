import { useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
import type { BattleState } from '@store/battle/types';

import { BattleHeader } from '../components/BattleHeader';
import { TimerBar } from '../components/TimerBar';
import { CritterDisplayCard } from '../components/CritterDisplayCard';
import { TeamDisplay } from '../components/TeamDisplay';
import { BattleLog } from '../components/BattleLog';
import { AbilitySelector } from '../components/AbilitySelector';

function BattlePage() {
  const { battleId } = useParams<{ battleId: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);

  const isConnected = useLobbyStore((state) => state.isConnected);
  const { player, opponent, actionLogHistory, timeRemaining, battleResult } = useBattleStore();

  useEffect(() => {
    const { subscribe, publish } = useLobbyStore.getState();
    const { setBattleState } = useBattleStore.getState();

    if (!isConnected || !battleId || !user?.id) return;

    const subscription = subscribe(`/topic/battle/${battleId}`, (newBattleState: BattleState) => {
      setBattleState(newBattleState, user.id);
      console.log(newBattleState)
    });

    publish(`/app/battle/${battleId}/join`, {});

    return () => {
      subscription?.unsubscribe();
    };
    
  }, [isConnected, battleId, user?.id, navigate]);

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

  const handleAbilityClick = useCallback((abilityId: string) => {
    const { publish } = useLobbyStore.getState();
    const { player } = useBattleStore.getState();

    if (!player.hasTurn || !battleId || !user?.id) {
      return;
    }

    const payload = {
      playerId: user.id,
      abilityId: abilityId,
    };
    
    publish(`/app/battle/${battleId}/ability`, payload);
  }, [battleId, user?.id]);
  
  return (
    <div className="min-h-screen bg-[#F8FFF8] p-4 sm:p-6 md:p-8">
      <div className="max-w-4xl mx-auto">
        <BattleHeader isPlayerTurn={player.hasTurn} />
        <TimerBar timeRemaining={timeRemaining} />

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 my-6">
          <CritterDisplayCard playerName={player.username} critter={player.activeCritter} />
          <CritterDisplayCard playerName={opponent.username} critter={opponent.activeCritter} />
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 my-6">
          <TeamDisplay title="Your Team" team={player.roster} />
          <TeamDisplay title="Opponent's Team" team={opponent.roster} />
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