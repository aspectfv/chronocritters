import type { BattleAbility, BattleEffect, CritterState } from "@store/battle/types";

export interface BattleHeaderProps {
  isPlayerTurn: boolean;
  onForfeit: () => void;
}

export interface TimerBarProps {
  timeRemaining: number;
  turnDuration: number;
}


export type AbilitySelectorProps = {
  abilities: BattleAbility[];
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
  isResolving: boolean;
};

export interface CritterDisplayCardProps {
  playerName: string;
  critter: CritterState;
  mirrored?: boolean;
  /** Changes whenever a fresh hit lands on this critter, which replays the animation. */
  hitTurn?: number;
  hitDamage?: number;
  hitEffectiveness?: number;
}

export interface StatusEffectChipsProps {
  effects: BattleEffect[];
  compact?: boolean;
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

export interface ForcedSwitchPanelProps {
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
