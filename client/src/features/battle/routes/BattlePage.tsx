import { useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@store/auth/useAuthStore';
import { useLobbyStore } from '@store/lobby/useLobbyStore';
import { useBattleStore } from '@store/battle/useBattleStore';
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
  
  // FIX: Select `isConnected` from the store hook here to use it as a dependency.
  const isConnected = useLobbyStore((state) => state.isConnected);

  // Select state values needed for rendering from the battle store.
  const { player, opponent, isPlayerTurn, timeRemaining, battleLog } = useBattleStore();

  // --- WebSocket Subscription and Battle Logic ---
  useEffect(() => {
    // Get stable function references via getState() to avoid including them as dependencies.
    const { subscribe, publish } = useLobbyStore.getState();
    const { updateBattleStateFromServer, setBattleState } = useBattleStore.getState();

    if (!isConnected || !battleId || !user?.id) {
      // Clear any stale battle data while waiting for a connection.
      setBattleState({
        player: { name: 'Connecting...', activeCritter: { name: '', currentHp: 0, maxHp: 100, stats: { atk: 0, def: 0, spd: 0 }, type: 'UNKNOWN' }, team: [], abilities: [] },
        opponent: { name: 'Connecting...', activeCritter: { name: '', currentHp: 0, maxHp: 100, stats: { atk: 0, def: 0, spd: 0 }, type: 'UNKNOWN' }, team: [], abilities: [] },
        battleLog: ['Connecting to battle...'],
      });
      return;
    }

    // Handler for incoming battle state updates from the server.
    const handleBattleUpdate = (serverBattleState: BattleStateResponse) => {
      updateBattleStateFromServer(serverBattleState, user.id);

      // Check for battle end condition to navigate away.
      if (serverBattleState.activePlayerId === null) {
        setTimeout(() => {
          navigate('/results', { replace: true });
        }, 3000); // 3-second delay to show the final message.
      }
    };

    // Subscribe to the main battle topic.
    const subscription = subscribe(`/topic/battle/${battleId}`, handleBattleUpdate);

    // After subscribing, send a 'join' message to get the initial state.
    publish(`/app/battle/${battleId}/join`, {});

    // Cleanup on component unmount.
    return () => {
      subscription?.unsubscribe();
    };

  // The effect re-runs only when the connection status or battle ID changes.
  }, [isConnected, battleId, user?.id, navigate]);

  // --- Player Action Handler ---
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
  
  // --- Render Component ---
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
        />
      </div>
    </div>
  );
}

export default BattlePage;