import { create } from 'zustand';
import {
  type BattleData,
  type BattleState,
  type PlayerState,
  type CritterState,
  BattleOutcome,
} from '@store/battle/types';
import { CritterType } from '@/gql/graphql';

function getMappedPlayers(playerOne: PlayerState, playerTwo: PlayerState, userId: string) {
  const isPlayerOne = playerOne.id === userId;

  const player = isPlayerOne ? playerOne : playerTwo;
  const opponent = isPlayerOne ? playerTwo : playerOne;

  return { player, opponent };
}

function withActiveCritter(playerState: PlayerState): PlayerState {
  const activeCritter = playerState.roster?.[playerState.activeCritterIndex];

  if (!activeCritter) {
    return playerState;
  }

  return { ...playerState, activeCritter };
}

const defaultEmptyCritter: CritterState = {
  id: '',
  name: '',
  type: CritterType.Unknown,
  stats: { maxHp: 100, currentHp: 100, currentAtk: 0, currentDef: 0 },
  abilities: [],
};

const defaultEmptyBattlePlayer: PlayerState = {
  id: '',
  username: '',
  hasTurn: false,
  activeCritterIndex: 0,
  roster: [defaultEmptyCritter],
  activeCritter: defaultEmptyCritter,
};

// Data only. Keeping the store's actions out of here is what lets
// resetBattleState clear the board without also wiping the setters.
const initialBattleData: BattleData = {
  battleId: '',
  activePlayerId: '',
  playerOne: defaultEmptyBattlePlayer,
  playerTwo: defaultEmptyBattlePlayer,
  actionLogHistory: ['Waiting for battle to start...'],
  battleStats: {
    turnCount: 0,
    battleStartTime: 0,
    duration: 0,
    playersDamageDealt: {},
    turnActionHistory: [],
  },
  battleOutcome: BattleOutcome.CONTINUE,
  disconnectedPlayerId: null,
  reconnectSecondsRemaining: 0,

  player: defaultEmptyBattlePlayer,
  opponent: defaultEmptyBattlePlayer,
  timeRemaining: 30,
};

export const useBattleStore = create<BattleState>((set) => ({
  ...initialBattleData,
  setBattleState: (newState, userId) =>
    set((prev) => {
      const updated: Partial<BattleData> = { ...newState };

      // Timer ticks arrive as a partial frame with no player data.
      if (updated.playerOne && updated.playerTwo) {
        const { player, opponent } = getMappedPlayers(updated.playerOne, updated.playerTwo, userId);

        updated.player = withActiveCritter(player);
        updated.opponent = withActiveCritter(opponent);
      }

      return { ...prev, ...updated };
    }),
  resetBattleState: () => set(initialBattleData),
}));
