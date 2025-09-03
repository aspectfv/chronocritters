import { useBattleStore } from '@store/battle/useBattleStore';
import { BattleHeader } from '../components/BattleHeader';
import { TimerBar } from '../components/TimerBar';
import { CritterDisplayCard } from '../components/CritterDisplayCard';
import { TeamDisplay } from '../components/TeamDisplay';
import { BattleLog } from '../components/BattleLog';
import { AbilitySelector } from '../components/AbilitySelector';

function BattlePage() {
  // Get all state and actions from the Zustand store
  const { player, opponent, isPlayerTurn, timeRemaining, battleLog, addLogMessage } = useBattleStore();

  // The handler function is defined once at the top level
  const handleAbilityClick = (abilityName: string) => {
    // In a real app, this would trigger a WebSocket publish event
    console.log(`Used ability: ${abilityName}`);
    addLogMessage(`${player.name}'s ${player.activeCritter.name} used ${abilityName}!`);
    // Here you would also update the opponent's HP, switch turns, etc.
  };

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