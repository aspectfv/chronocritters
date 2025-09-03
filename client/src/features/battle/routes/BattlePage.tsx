import { useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
import { CritterType } from '@store/battle/types';
import type { BattleStateResponse } from '@store/battle/types';

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
  const { player, opponent, isPlayerTurn, timeRemaining, battleLog } = useBattleStore();

  useEffect(() => {
    const { subscribe, publish } = useLobbyStore.getState();
    const { updateBattleStateFromServer, setBattleState } = useBattleStore.getState();

    if (!isConnected || !battleId || !user?.id) {
      setBattleState({
        player: { name: 'Connecting...', activeCritter: { name: '', currentHp: 0, maxHp: 100, stats: { atk: 0, def: 0, spd: 0 }, type: CritterType.UNKNOWN }, team: [], abilities: [] },
        opponent: { name: 'Connecting...', activeCritter: { name: '', currentHp: 0, maxHp: 100, stats: { atk: 0, def: 0, spd: 0 }, type: CritterType.UNKNOWN }, team: [], abilities: [] },
        battleLog: ['Connecting to battle...'],
      });
      return;
    }

    const subscription = subscribe(`/topic/battle/${battleId}`, (serverBattleState: BattleStateResponse) => {
      updateBattleStateFromServer(serverBattleState, user.id);
    });

    publish(`/app/battle/${battleId}/join`, {});

    return () => {
      subscription?.unsubscribe();
    };
    
  }, [isConnected, battleId, user?.id, navigate]);

  const handleAbilityClick = useCallback((abilityId: string) => {
    const { publish } = useLobbyStore.getState();
    const { isPlayerTurn } = useBattleStore.getState();

    if (!isPlayerTurn || !battleId || !user?.id) {
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
        <BattleHeader isPlayerTurn={isPlayerTurn} />
        <TimerBar timeRemaining={timeRemaining} />

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 my-6">
          <CritterDisplayCard playerName={player.name} critter={player.activeCritter} />
          <CritterDisplayCard playerName={opponent.name} critter={opponent.activeCritter} />
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 my-6">
          <TeamDisplay title="Your Team" team={player.team} />
          <TeamDisplay title="Opponent's Team" team={opponent.team} />
        </div>

        <BattleLog log={battleLog} />
        
        <AbilitySelector 
          abilities={player.abilities} 
          onAbilityClick={handleAbilityClick}
          isPlayerTurn={isPlayerTurn} 
          critterType={player.activeCritter.type}
        />
      </div>
    </div>
  );
}

export default BattlePage;