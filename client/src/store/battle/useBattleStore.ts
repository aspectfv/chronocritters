import { create } from 'zustand';
import { CritterType } from '@store/battle/types';
import type {
  BattleState,
  BattlePlayer,
  BattleCritter,
  TeamCritter,
  Ability,
  BattleStateResponse,
  PlayerState,
  CritterState,
} from '@store/battle/types';

const defaultEmptyCritter: BattleCritter = {
  name: 'No Critter',
  type: CritterType.UNKNOWN,
  stats: { maxHp: 100, currentHp: 100, atk: 0, def: 0 },
};

const defaultEmptyBattlePlayer: BattlePlayer = {
  name: 'Waiting for player...',
  activeCritter: defaultEmptyCritter,
  team: [],
  abilities: [],
  hasTurn: false,
};

const initialState = {
  player: defaultEmptyBattlePlayer,
  opponent: defaultEmptyBattlePlayer,
  isPlayerTurn: false,
  timeRemaining: 30,
  battleLog: ['Waiting for battle to start...'],
  battleResult: null,
};

const getAbilityDescription = (ability: Ability): string => {
  switch (ability.type) {
    case 'ATTACK':
      return `A powerful strike dealing ${ability.power} damage.`;
    case 'DEFENSE':
      return `Boosts defense by ${ability.power} points.`;
    case 'SUPPORT':
      return `Restores ${ability.power} health.`;
    default:
      return 'An ability with a mysterious effect.';
  }
};

const mapCritterStateToBattleCritter = (
  critterState: CritterState
): BattleCritter => ({
  name: critterState.name,
  type: critterState.type,
  stats: {
    currentHp: critterState.stats.currentHp,
    maxHp: critterState.stats.maxHp,
    atk: critterState.stats.currentAtk,
    def: critterState.stats.currentDef,
  },
});

const mapCritterStateToTeamCritter = (
  critterState: CritterState
): TeamCritter => ({
  name: critterState.name,
  type: critterState.type,
  currentHp: critterState.stats.currentHp,
  maxHp: critterState.stats.maxHp,
});

const mapPlayerStateToBattlePlayer = (
  playerState: PlayerState,
  isCurrentUser: boolean
): BattlePlayer => {
  const activeCritterState = playerState.roster[playerState.activeCritterIndex];

  const activeCritter: BattleCritter = activeCritterState
    ? mapCritterStateToBattleCritter(activeCritterState)
    : defaultEmptyCritter;

  const abilities: Ability[] = (isCurrentUser && activeCritterState?.abilities)
    ? activeCritterState.abilities.map(ab => ({ ...ab, description: getAbilityDescription(ab) }))
    : [];

  const team: TeamCritter[] = playerState.roster.map(mapCritterStateToTeamCritter);

  return {
    name: playerState.username,
    activeCritter,
    team,
    abilities,
    hasTurn: playerState.hasTurn,
  };
};

export const useBattleStore = create<BattleState>((set) => ({
  ...initialState,

  setBattleState: (newState) => set(newState),
  addLogMessage: (message) => set((state) => ({ battleLog: [...state.battleLog, message] })),
  resetBattleState: () => set(initialState),

  updateBattleStateFromServer: (serverBattleState: BattleStateResponse, currentUserId: string) => {
    if (!currentUserId) {
      console.error("Battle state update failed: currentUserId is not provided.");
      return;
    }

    const isPlayerOneUser = serverBattleState.playerOne.id === currentUserId;
    const userPlayerState = isPlayerOneUser ? serverBattleState.playerOne : serverBattleState.playerTwo;
    const opponentPlayerState = isPlayerOneUser ? serverBattleState.playerTwo : serverBattleState.playerOne;

    const player = mapPlayerStateToBattlePlayer(userPlayerState, true);
    const opponent = mapPlayerStateToBattlePlayer(opponentPlayerState, false);

    const newLog = serverBattleState.actionLogHistory || [];

    set({
      player,
      opponent,
      timeRemaining: 30,
      battleLog: newLog,
    });
  },
}));