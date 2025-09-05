export enum AbilityType {
  ATTACK = 'ATTACK',
  DEFENSE = 'DEFENSE',
  SUPPORT = 'SUPPORT',
  UNKNOWN = 'UNKNOWN'
}

export enum CritterType {
  FIRE = 'FIRE',
  WATER = 'WATER',
  GRASS = 'GRASS',
  ELECTRIC = 'ELECTRIC',
  UNKNOWN = 'UNKNOWN'
}

export interface CurrentStats {
  maxHp: number;
  currentHp: number;
  currentAtk: number;
  currentDef: number;
}

export interface Ability {
  id: string;
  name: string;
  description?: string;
  type: AbilityType;
  power: number;
}

export interface CritterState {
  id: string;
  name: string;
  type: CritterType;
  stats: CurrentStats;
  abilities: Ability[];
}

export interface BattleCritter {
  name: string;
  type: CritterType;
  stats: {
    currentHp: number;
    maxHp: number;
    atk: number;
    def: number;
  };
}
export interface TeamCritter {
  name: string;
  type: CritterType;
  currentHp: number;
  maxHp: number;
}

export interface PlayerState {
  id: string;
  username: string;
  hasTurn: boolean;
  activeCritterIndex: number;
  roster: CritterState[];
}

export interface BattlePlayer {
  name: string;
  hasTurn: boolean;
  activeCritter: BattleCritter;
  team: TeamCritter[];
  abilities: Ability[];
}

export interface BattleStateResponse {
  battleId: string;
  activePlayerId: string | null;
  playerOne: PlayerState;
  playerTwo: PlayerState;
  lastActionLog: string;
}

export interface BattleState {
  player: BattlePlayer;
  opponent: BattlePlayer;
  timeRemaining: number;
  battleLog: string[];
  battleResult: 'victory' | 'defeat' | null;
  setBattleState: (newState: Partial<BattleState>) => void;
  addLogMessage: (message: string) => void;
  resetBattleState: () => void;
  updateBattleStateFromServer: (serverBattleState: BattleStateResponse, currentUserId: string) => void;
}

export interface executeAbilityRequest {
  battleId: string;
  playerId: string;
  abilityId: string;
}