import type { CritterState } from "@store/battle/types";
import type { Ability, CritterType } from "src/gql/graphql";

export interface BattleHeaderProps {
  isPlayerTurn: boolean;
}

export type AbilitySelectorProps = {
  abilities: Ability[];
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
  critterType: CritterType;
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