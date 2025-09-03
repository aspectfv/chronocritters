import { create } from 'zustand';
import { CritterType } from '@store/battle/types';
import type {
  BattleState,
  BattlePlayer,
  BattleCritter,
  TeamCritter,
  Ability,
  BattleStateResponse,
  PlayerStateResponse,
  CritterStateResponse,
  AbilityResponse,
} from '@store/battle/types';

const defaultEmptyCritter: BattleCritter = {
  name: 'No Critter',
  type: CritterType.UNKNOWN,
  currentHp: 0,
  maxHp: 100,
  stats: { atk: 0, def: 0 },
};

const defaultEmptyBattlePlayer: BattlePlayer = {
  name: 'Waiting for player...',
  activeCritter: defaultEmptyCritter,
  team: [],
  abilities: [],
};

const getAbilityDescription = (ability: AbilityResponse): string => {
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

const mapAbilityResponseToAbility = (
  abilityResponse: AbilityResponse,
): Ability => ({
  id: abilityResponse.id,
  name: abilityResponse.name,
  description: getAbilityDescription(abilityResponse),
  type: abilityResponse.type,
  power: abilityResponse.power,
});

const mapCritterStateResponseToBattleCritter = (
  critterState: CritterStateResponse
): BattleCritter => ({
  name: critterState.name,
  type: critterState.type,
  currentHp: critterState.stats.currentHp,
  maxHp: critterState.stats.maxHp,
  stats: {
    atk: critterState.stats.currentAtk,
    def: critterState.stats.currentDef,
  },
});

const mapCritterStateResponseToTeamCritter = (
  critterState: CritterStateResponse
): TeamCritter => ({
  name: critterState.name,
  type: critterState.type,
  currentHp: critterState.stats.currentHp,
  maxHp: critterState.stats.maxHp,
});

const mapPlayerStateResponseToBattlePlayer = (
  playerStateResponse: PlayerStateResponse,
  isCurrentUser: boolean
): BattlePlayer => {
  const activeCritterState = playerStateResponse.roster[playerStateResponse.activeCritterIndex];

  const activeCritter: BattleCritter = activeCritterState
    ? mapCritterStateResponseToBattleCritter(activeCritterState)
    : defaultEmptyCritter;

  const abilities: Ability[] = (isCurrentUser && activeCritterState?.abilities)
    ? activeCritterState.abilities.map(ab => mapAbilityResponseToAbility(ab))
    : [];

  const team: TeamCritter[] = playerStateResponse.roster.map(mapCritterStateResponseToTeamCritter);

  return {
    name: playerStateResponse.username,
    activeCritter,
    team,
    abilities,
  };
};

export const useBattleStore = create<BattleState>((set, get) => ({
  player: defaultEmptyBattlePlayer,
  opponent: defaultEmptyBattlePlayer,
  isPlayerTurn: false,
  timeRemaining: 30,
  battleLog: ['Waiting for battle to start...'],

  setBattleState: (newState) => set(newState),
  addLogMessage: (message) => set((state) => ({ battleLog: [...state.battleLog, message] })),

  updateBattleStateFromServer: (serverBattleState: BattleStateResponse, currentUserId: string) => {
    if (!currentUserId) {
      console.error("Battle state update failed: currentUserId is not provided.");
      return;
    }

    const isPlayerOneUser = serverBattleState.playerOne.id === currentUserId;
    const userPlayerState = isPlayerOneUser ? serverBattleState.playerOne : serverBattleState.playerTwo;
    const opponentPlayerState = isPlayerOneUser ? serverBattleState.playerTwo : serverBattleState.playerOne;

    const player = mapPlayerStateResponseToBattlePlayer(userPlayerState, true);
    const opponent = mapPlayerStateResponseToBattlePlayer(opponentPlayerState, false);

    const currentLog = get().battleLog;
    const lastLogFromServer = serverBattleState.lastActionLog;
    const newLog = (lastLogFromServer && !currentLog.includes(lastLogFromServer))
      ? [...currentLog, lastLogFromServer]
      : currentLog;

    set({
      player,
      opponent,
      isPlayerTurn: serverBattleState.activePlayerId === currentUserId,
      timeRemaining: 30,
      battleLog: newLog,
    });
  },
}));