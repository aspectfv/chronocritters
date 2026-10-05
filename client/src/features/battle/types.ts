import type { BattleAbility, BattleEffect, CritterState } from "@store/battle/types";
import type { CritterType } from "@/gql/graphql";

export interface BattleHeaderProps {
  isPlayerTurn: boolean;
  onForfeit: () => void;
}

export interface StatusEffectChipsProps {
  effects: BattleEffect[];
  compact?: boolean;
}

export interface BattleLogProps {
  log: string[];
}

export interface ForcedSwitchPanelProps {
  open: boolean;
  team: CritterState[];
  onCritterClick: (targetCritterIndex: number) => void;
  disabled: boolean;
}

export interface OpponentStatusBannerProps {
  opponentName: string;
  secondsRemaining: number;
}

export interface BattleLoaderParams {
  params: {
    battleId?: string;
  };
}

export interface CritterCellProps {
  playerName: string;
  critter: CritterState;
  side: 'player' | 'opponent';
  showNumericHp?: boolean;
  /** Changes whenever a fresh hit lands here, which replays the animation. */
  hitTurn?: number;
  hitDamage?: number;
  hitEffectiveness?: number;
}

export interface ChronoDialProps {
  timeRemaining: number;
  turnDuration: number;
}

export interface MoveGridProps {
  abilities: BattleAbility[];
  casterType: CritterType;
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
  isResolving: boolean;
}

export interface TeamRailProps {
  title: string;
  team: CritterState[];
  activeCritterId: string;
  align: 'left' | 'right';
  canSwitch?: boolean;
  onCritterClick?: (targetCritterIndex: number) => void;
}
