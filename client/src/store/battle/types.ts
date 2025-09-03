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
  type: AbilityType;
}

export interface CritterStateResponse {
  id: string;
  name: string;
  type: CritterType;
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
  type: CritterType;
  currentHp: number;
  maxHp: number;
  stats: {
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

export interface Ability {
  id: string;
  name: string;
  description: string;
  type: AbilityType;
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
  updateBattleStateFromServer: (serverBattleState: BattleStateResponse, currentUserId: string) => void;
}

export interface executeAbilityRequest {
  battleId: string;
  playerId: string;
  abilityId: string;
}

export type AbilitySelectorProps = {
  abilities: Ability[];
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
  critterType: CritterType; // Add this line
};

export interface CritterDisplayCardProps {
  playerName: string;
  critter: BattleCritter;
}