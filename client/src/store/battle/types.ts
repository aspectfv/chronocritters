import type { Ability, CritterType } from "src/gql/graphql";

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

export interface BattleRewards {
  playersExpGained: {
    [playerId: string]: number;
  };
  crittersExpGained: {
    [critterId: string]: number;
  };
}


export interface BattleState {
  battleId: string;
  activePlayerId: string;
  playerOne: PlayerState;
  playerTwo: PlayerState;
  actionLogHistory: string[];
  playersDamageDealt: {
    [playerId: string]: number;
  };
  turnCount: number;
  battleStartTime: number;
  battleOutcome: BattleOutcome;
  winnerId?: string;
  battleRewards?: BattleRewards;

  // client specific props
  player: PlayerState;
  opponent: PlayerState;
  timeRemaining: number;
  setBattleState: (newState: Partial<BattleState>, userId: string) => void;
  addLogMessage: (message: string) => void;
  resetBattleState: () => void;
}