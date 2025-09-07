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
  STEEL = 'STEEL',
  UNKNOWN = 'UNKNOWN'
}

export interface Ability {
  id: string;
  name: string;
  description?: string;
  type: AbilityType;
  power: number;
}

export interface CurrentStats {
  maxHp: number;
  currentHp: number;
  currentAtk: number;
  currentDef: number;
}

export interface CritterState {
  id: string;
  name: string;
  type: CritterType;
  stats: CurrentStats;
  abilities: Ability[];
}

export interface PlayerState {
  id: string;
  username: string;
  hasTurn: boolean;
  activeCritterIndex: number;
  roster: CritterState[];

  // client specific props
  activeCritter: CritterState;
}

export interface BattleState {
  battleId: string;
  activePlayerId: string;
  playerOne: PlayerState;
  playerTwo: PlayerState;
  actionLogHistory: string[];

  // client specific props
  player: PlayerState;
  opponent: PlayerState;
  timeRemaining: number;
  battleResult: 'victory' | 'defeat' | null;
  setBattleState: (newState: Partial<BattleState>, userId: string) => void;
  addLogMessage: (message: string) => void;
  resetBattleState: () => void;
}