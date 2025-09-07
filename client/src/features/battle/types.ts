import type { Ability, CritterState, CritterType } from "@store/battle/types";

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
}