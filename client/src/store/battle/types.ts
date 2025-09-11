export enum CritterType {
  FIRE = 'FIRE',
  WATER = 'WATER',
  GRASS = 'GRASS',
  ELECTRIC = 'ELECTRIC',
  METAL = 'METAL',
  TOXIC = 'TOXIC',
  KINETIC = 'KINETIC',
  UNKNOWN = 'UNKNOWN'
}

export enum EffectType {
  DAMAGE = 'DAMAGE',
  DAMAGE_OVER_TIME = 'DAMAGE_OVER_TIME',
  SKIP_TURN = 'SKIP_TURN',
  BUFF = 'BUFF',
  DEBUFF = 'DEBUFF'
}


export interface SkipTurnEffect extends Effect {
  duration: number;
}

export interface DamageOverTimeEffect extends Effect {
  damagePerTurn: number;
  duration: number;
}

export interface DamageEffect extends Effect {
  damage: number;
}

export type AbilityEffect = DamageEffect | DamageOverTimeEffect | SkipTurnEffect;

export interface Effect {
  id: string;
  type: EffectType;
  description?: string;
}

export interface Ability {
  id: string;
  name: string;
  effects: AbilityEffect[];
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