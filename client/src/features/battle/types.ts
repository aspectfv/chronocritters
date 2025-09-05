import type { Ability, BattleCritter, CritterType } from "@store/battle/types";

export type AbilitySelectorProps = {
  abilities: Ability[];
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
  critterType: CritterType;
};

export interface CritterDisplayCardProps {
  playerName: string;
  critter: BattleCritter;
}