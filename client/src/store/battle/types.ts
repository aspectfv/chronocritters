import type { CritterType } from "src/gql/graphql";

// The battle payload comes over REST from gamelogic, which discriminates effect
// subtypes with `_type`. The GraphQL schema uses `__typename` for the same
// objects, so the two shapes are kept apart rather than shared.
export type BattleEffect =
  | { _type: 'DamageEffect'; id: string; description: string; damage: number }
  | { _type: 'DamageOverTimeEffect'; id: string; description: string; damagePerTurn: number; duration: number }
  | { _type: 'SkipTurnEffect'; id: string; description: string; duration: number };

export interface BattleAbility {
  id: string;
  name: string;
  description: string;
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
  currentAtk: number;
  currentDef: number;
}

export interface CritterState {
  id: string;
  name: string;
  type: CritterType;
  stats: CurrentStats;
  abilities: BattleAbility[];
  activeStatusEffects: BattleEffect[];
  fainted: boolean;
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

  /** The player who has lost their active critter and owes a free replacement. */
  awaitingSwitchPlayerId?: string | null;

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