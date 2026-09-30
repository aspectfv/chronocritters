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
  /** Whether this side can still rewind, shown as a charge on the plaque. */
  rewindCharged: boolean;
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
  /** Turns left on each ability that is cooling down, keyed by ability id. */
  cooldowns: Record<string, number>;
  onAbilityClick: (abilityId: string) => void;
  isPlayerTurn: boolean;
  isResolving: boolean;
  rewindUsed: boolean;
  /** A rewind needs the player's turn, an unspent charge and two turns behind it. */
  canRewind: boolean;
  onRewind: () => void;
}

export interface TeamRailProps {
  title: string;
  team: CritterState[];
  activeCritterId: string;
  align: 'left' | 'right';
  canSwitch?: boolean;
  onCritterClick?: (targetCritterIndex: number) => void;
}
