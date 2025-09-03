import { create } from 'zustand';
import type { BattleState, BattlePlayer, BattleCritter, Ability } from './types';

// Mock data that matches your UI design for initial display
const initialPlayer: BattlePlayer = {
  name: 'DragonMaster',
  activeCritter: {
    name: 'Flamewyrm',
    type: 'Fire',
    currentHp: 95,
    maxHp: 120,
    stats: { atk: 85, def: 70, spd: 60 },
  },
  team: [
    { name: 'Aquadrake', type: 'Water', currentHp: 110, maxHp: 110 },
    { name: 'Earthshaker', type: 'Ground', currentHp: 130, maxHp: 130 },
    { name: 'Windstorm', type: 'Grass', currentHp: 90, maxHp: 90 },
  ],
  abilities: [
    { name: 'Fire Blast', description: 'A powerful fire attack that may burn the opponent.', type: 'Fire', power: 90 },
    { name: 'Dragon Claw', description: 'Sharp claws that deal consistent damage.', type: 'Dragon', power: 80 },
    { name: 'Flame Shield', description: 'Raises defense and may burn attackers.', type: 'Fire', power: 0 },
    { name: 'Inferno', description: 'Ultimate fire attack with high critical chance.', type: 'Fire', power: 110 },
  ]
};

const initialOpponent: BattlePlayer = {
  name: 'StormCaller',
  activeCritter: {
    name: 'Thunderbeast',
    type: 'Electric',
    currentHp: 78,
    maxHp: 100,
    stats: { atk: 90, def: 65, spd: 80 },
  },
  team: [
    { name: 'Frostbite', type: 'Water', currentHp: 105, maxHp: 105 },
  ],
  abilities: [] // Opponent's abilities are not shown
};

export const useBattleStore = create<BattleState>((set) => ({
  player: initialPlayer,
  opponent: initialOpponent,
  isPlayerTurn: true,
  timeRemaining: 27,
  battleLog: ['Battle begins! Choose your first move!'],
  setBattleState: (newState) => set(newState),
  addLogMessage: (message) => set((state) => ({ battleLog: [...state.battleLog, message] })),
}));