import type { CritterState } from "@store/battle/types";
import type { Ability } from "src/gql/graphql";

export interface BattleHeaderProps {
  isPlayerTurn: boolean;
  onForfeit: () => void;
}

export interface TimerBarProps {
  timeRemaining: number;
}


export type AbilitySelectorProps = {
  abilities: Ability[];
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
};

export interface CritterDisplayCardProps {
  playerName: string;
  critter: CritterState;
}

export interface BattleLogProps {
  log: string[];
}

export interface TeamDisplayProps {
  title: string;
  team: CritterState[];
  activeCritterId: string;
  isPlayerTurn: boolean;
  onCritterClick: (targetCritterIndex: number) => void;
}

export interface BattleLoaderParams {
  params: {
    battleId?: string;
  };
}