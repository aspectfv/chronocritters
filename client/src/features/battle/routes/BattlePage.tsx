import { useEffect, useCallback } from 'react';
import { useParams, useNavigate, useLoaderData } from 'react-router-dom';
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
import { BattleMusicControl } from '@features/battle/components/BattleMusicControl';
import { executeAbility, switchCritter } from '@api/gamelogic';
import { ConnectionStatus } from '@store/lobby/types';

function BattlePage() {
  const { battleId } = useParams<{ battleId: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const initialBattleState = useLoaderData() as BattleState;

  const isConnected = useLobbyStore((state) => state.connectionStatus === ConnectionStatus.CONNECTED);
  const { player, opponent, actionLogHistory, timeRemaining } = useBattleStore();

  useEffect(() => {
    const { setBattleState } = useBattleStore.getState();
    if (initialBattleState && user?.id) {
      setBattleState(initialBattleState, user.id);
    }
  }, [initialBattleState, user?.id]);

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
    const { rewards } = useBattleStore.getState();
    
    if (opponent.roster.length > 0 && opponent.roster.every(critter => critter.stats.currentHp <= 0)) {
      navigate(`/results/${battleId}`, { state: { result: 'victory', rewards } });
    } else if (player.roster.length > 0 && player.roster.every(critter => critter.stats.currentHp <= 0)) {
      navigate(`/results/${battleId}`, { state: { result: 'defeat', rewards } });
    }
  }, [player, opponent, navigate, battleId]);

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
    <div className="min-h-screen bg-[#f0f7f3] p-4">
      <div className="max-w-screen-xl mx-auto relative">
        <BattleMusicControl />
        <BattleHeader isPlayerTurn={player.hasTurn} />
        <TimerBar timeRemaining={timeRemaining} />

        <div className="grid grid-cols-1 lg:grid-cols-[2.5fr_3fr_2.5fr] gap-4 mt-4">
          <div className="flex flex-col gap-4">
            <CritterDisplayCard playerName={player.username} critter={player.activeCritter} />
            <TeamDisplay title="Your Team" team={player.roster} activeCritterId={player.activeCritter.id} isPlayerTurn={player.hasTurn} onCritterClick={handleSwitchCritter} />
          </div>

          <div className="flex flex-col gap-4">
            <BattleLog log={actionLogHistory} />
            <AbilitySelector 
              abilities={player.activeCritter.abilities} 
              onAbilityClick={handleAbilityClick}
              isPlayerTurn={player.hasTurn} 
              critterType={player.activeCritter.type}
            />
          </div>

          <div className="flex flex-col gap-4">
            <CritterDisplayCard playerName={opponent.username} critter={opponent.activeCritter} />
            <TeamDisplay title="Opponent's Team" team={opponent.roster} activeCritterId={opponent.activeCritter.id} isPlayerTurn={false} onCritterClick={() => {}} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default BattlePage;