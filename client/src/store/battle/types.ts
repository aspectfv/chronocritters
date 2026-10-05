import type { CritterType, Stat, StatusCondition } from "src/gql/graphql";

// The battle payload comes over REST from gamelogic, which discriminates effect
// subtypes with `_type`. The GraphQL schema uses `__typename` for the same
// objects, so the two shapes are kept apart rather than shared.
export type BattleEffect =
  | { _type: 'DamageEffect'; id: string; description: string; damage: number }
  | { _type: 'DamageOverTimeEffect'; id: string; description: string; damagePerTurn: number; duration: number }
  | { _type: 'StatModifierEffect'; id: string; description: string; stat: Stat; stages: number }
  | { _type: 'HealEffect'; id: string; description: string; healPercent: number }
  | { _type: 'LifestealEffect'; id: string; description: string; damage: number; drainPercent: number }
  | { _type: 'ShieldEffect'; id: string; description: string; amount: number; duration: number }
  | { _type: 'CleanseEffect'; id: string; description: string }
  | { _type: 'PriorityEffect'; id: string; description: string }
  | { _type: 'StatusEffect'; id: string; description: string; condition: StatusCondition; duration: number; potency: number };

export type BattleStatus = Extract<BattleEffect, { _type: 'StatusEffect' }>;

export interface BattleAbility {
  id: string;
  name: string;
  description: string;
  cooldownTurns: number;
  effects: BattleEffect[];
}

export interface TurnResult {
  turn: number;
  casterCritterId: string;
  targetCritterId: string;
  damage: number;
  effectiveness: number;
}

export enum BattleOutcome {
  CONTINUE = 'CONTINUE',
  BATTLE_END = 'BATTLE_END',
}
export interface CurrentStats {
  maxHp: number;
  currentHp: number;
  attack: number;
  defense: number;
  speed: number;
  /** Stage shifts by stat, -6 to +6. A stat at stage zero is absent. */
  stages: Partial<Record<Stat, number>>;
  /** The base stats read through their stages. */
  currentAtk: number;
  currentDef: number;
  currentSpd: number;
}

export interface CritterState {
  id: string;
  name: string;
  type: CritterType;
  stats: CurrentStats;
  abilities: BattleAbility[];
  activeStatusEffects: BattleEffect[];
  /** The one status condition held, kept apart from the effect list because two cannot stack. */
  status: BattleStatus | null;
  /** The condition that has just ended and cannot land again this turn. */
  statusImmunity: StatusCondition | null;
  fainted: boolean;

  /** Turns left on each ability that is cooling down, keyed by ability id. */
  abilityCooldowns: Record<string, number>;
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

export interface BattleRewards {
  playersExpGained: {
    [playerId: string]: number;
  };
  crittersExpGained: {
    [critterId: string]: number;
  };
}

export interface TurnActionEntry {
  playerId: string;
  playerHasTurn: boolean;
  turn: number;
  turnActionLog: string;
}

export interface BattleStats {
  turnCount: number;
  battleStartTime: number;
  duration: number;
  playersDamageDealt: {
    [playerId: string]: number;
  }
  turnActionHistory: TurnActionEntry[];
}


export interface BattleData {
  battleId: string;
  activePlayerId: string;
  playerOne: PlayerState;
  playerTwo: PlayerState;
  actionLogHistory: string[];
  battleStats: BattleStats;
  battleOutcome: BattleOutcome;
  winnerId?: string;
  battleRewards?: BattleRewards;

  turnDuration: number;
  lastTurnResult?: TurnResult | null;

  /** The players who have lost their active critter and owe a free replacement. */
  awaitingSwitchPlayerIds: string[];

  // Set while a player is inside their reconnect window, cleared when they
  // return or when the window runs out and the battle ends.
  disconnectedPlayerId?: string | null;
  reconnectSecondsRemaining?: number;

  // client specific props
  player: PlayerState;
  opponent: PlayerState;
  timeRemaining: number;
}

export interface BattleState extends BattleData {
  setBattleState: (newState: Partial<BattleData>, userId: string) => void;
  resetBattleState: () => void;
}