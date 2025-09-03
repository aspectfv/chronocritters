export interface CurrentStatsResponse {
  maxHp: number;
  currentHp: number;
  currentAtk: number;
  currentDef: number;
}

export interface AbilityResponse {
  id: string;
  name: string;
  power: number;
  type: 'ATTACK' | 'DEFENSE' | 'SUPPORT';
}

export interface CritterStateResponse {
  id: string;
  name: string;
  type: 'FIRE' | 'WATER' | 'GRASS' | 'ELECTRIC';
  stats: CurrentStatsResponse;
  abilities: AbilityResponse[];
}

export interface PlayerStateResponse {
  id: string;
  username: string;
  hasTurn: boolean;
  activeCritterIndex: number;
  roster: CritterStateResponse[];
}

export interface BattleStateResponse {
  battleId: string;
  activePlayerId: string | null; // Null when the battle is over
  playerOne: PlayerStateResponse;
  playerTwo: PlayerStateResponse;
  lastActionLog: string;
}

export interface BattleCritter {
  name: string;
  type: string;
  currentHp: number;
  maxHp: number;
  stats: {
    atk: number;
    def: number;
    spd: number;
  };
}

export interface TeamCritter {
  name: string;
  type: string;
  currentHp: number;
  maxHp: number;
}

export interface Ability {
  name: string;
  description: string;
  type: string;
  power: number;
}

export interface BattlePlayer {
  name: string;
  activeCritter: BattleCritter;
  team: TeamCritter[];
  abilities: Ability[];
}

export interface BattleState {
  player: BattlePlayer;
  opponent: BattlePlayer;
  isPlayerTurn: boolean;
  timeRemaining: number;
  battleLog: string[];
  setBattleState: (newState: Partial<BattleState>) => void;
  addLogMessage: (message: string) => void;
}

export interface executeAbilityRequest {
  battleId: string;
  playerId: string;
  abilityId: string;
}
