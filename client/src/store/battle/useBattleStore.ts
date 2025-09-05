import { create } from 'zustand';
import { AbilityType, CritterType } from '@store/battle/types';
import type {
  BattleState,
  Ability,
  PlayerState,
  CritterState,
} from '@store/battle/types';

const getAbilityDescription = (ability: Ability): string => {
  switch (ability.type) {
    case AbilityType.ATTACK:
      return `A powerful strike dealing ${ability.power} damage.`;
    case AbilityType.DEFENSE:
      return `Boosts defense by ${ability.power} points.`;
    case AbilityType.SUPPORT:
      return `Restores ${ability.power} health.`;
    default:
      return 'An ability with a mysterious effect.';
  }
};

function getMappedPlayers(playerOne: PlayerState, playerTwo: PlayerState, userId: string) {
  const isPlayerOne = playerOne.id === userId;

  const player = isPlayerOne ? playerOne : playerTwo;
  const opponent = isPlayerOne ? playerTwo : playerOne;

  return { player, opponent };
}

const defaultEmptyCritter: CritterState = {
  id: '',
  name: '',
  type: CritterType.UNKNOWN,
  stats: { maxHp: 100, currentHp: 100, currentAtk: 0, currentDef: 0 },
  abilities: [],
};

const defaultEmptyBattlePlayer: PlayerState = {
  id: '',
  username: '',
  hasTurn: false,
  activeCritterIndex: 0,
  roster: [defaultEmptyCritter],
  activeCritter: defaultEmptyCritter
};

const initialState: BattleState = {
  battleId: '',
  activePlayerId: '',
  playerOne: defaultEmptyBattlePlayer,
  playerTwo: defaultEmptyBattlePlayer,
  actionLogHistory: ['Waiting for battle to start...'],

  player: defaultEmptyBattlePlayer,
  opponent: defaultEmptyBattlePlayer,
  timeRemaining: 30,
  battleResult: null,
  setBattleState: () => {},
  addLogMessage: () => {},
  resetBattleState: () => {},
};

export const useBattleStore = create<BattleState>((set) => ({
  ...initialState,
  setBattleState: (newState, userId?) =>
    set((prev) => {
      const updated: Partial<BattleState> = { ...newState };

      if (
        updated.playerOne &&
        updated.playerTwo &&
        updated.activePlayerId
      ) {
        const { player, opponent } = getMappedPlayers(
          updated.playerOne ?? prev.playerOne,
          updated.playerTwo ?? prev.playerTwo,
          userId ?? ""
        );

        if (player.roster && typeof player.activeCritterIndex === 'number') {
          const activeCritter = player.roster[player.activeCritterIndex];
          if (activeCritter && activeCritter.abilities) {
            player.activeCritter = {
              ...activeCritter,
              abilities: activeCritter.abilities.map((ab) => ({
                ...ab,
                description: getAbilityDescription(ab),
              })),
            };
          }
        }

        if (opponent.roster && typeof opponent.activeCritterIndex === 'number') {
          const activeCritter = opponent.roster[opponent.activeCritterIndex];
          if (activeCritter && activeCritter.abilities) {
            opponent.activeCritter = {
              ...activeCritter,
              abilities: activeCritter.abilities.map((ab) => ({
                ...ab,
                description: getAbilityDescription(ab),
              })),
            };
          }
        }

        updated.player = player;
        updated.opponent = opponent;
      }

      return { ...prev, ...updated };
    }),
  resetBattleState: () => set(initialState),
}));