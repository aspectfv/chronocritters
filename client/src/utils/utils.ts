import { ConnectionStatus } from "@store/lobby/types";
import type { BattleEffect } from "@store/battle/types";
import { CritterType, type EffectUnion } from "@/gql/graphql";

export const critterTypeIcons: Record<CritterType, string> = {
  [CritterType.Fire]: '🔥',
  [CritterType.Water]: '💧',
  [CritterType.Electric]: '⚡',
  [CritterType.Grass]: '🌿',
  [CritterType.Metal]: '⛓️',
  [CritterType.Toxic]: '☠️',
  [CritterType.Kinetic]: '🌀',
  [CritterType.Unknown]: '❓',
};

export const critterTypeStyles: Record<CritterType, string> = {
  [CritterType.Fire]: 'bg-type-fire/15 text-type-fire border-type-fire/35',
  [CritterType.Water]: 'bg-type-water/15 text-type-water border-type-water/35',
  [CritterType.Grass]: 'bg-type-grass/15 text-type-grass border-type-grass/35',
  [CritterType.Electric]: 'bg-type-electric/20 text-type-electric border-type-electric/40',
  [CritterType.Metal]: 'bg-type-metal/15 text-type-metal border-type-metal/35',
  [CritterType.Toxic]: 'bg-type-toxic/15 text-type-toxic border-type-toxic/35',
  [CritterType.Kinetic]: 'bg-type-kinetic/15 text-type-kinetic border-type-kinetic/35',
  [CritterType.Unknown]: 'bg-type-unknown/15 text-type-unknown border-type-unknown/35',
};

/** Solid fills, for the move grid and cell rims where the type must read at a glance. */
const critterTypeFills: Record<CritterType, string> = {
  [CritterType.Fire]: 'bg-type-fire',
  [CritterType.Water]: 'bg-type-water',
  [CritterType.Grass]: 'bg-type-grass',
  [CritterType.Electric]: 'bg-type-electric',
  [CritterType.Metal]: 'bg-type-metal',
  [CritterType.Toxic]: 'bg-type-toxic',
  [CritterType.Kinetic]: 'bg-type-kinetic',
  [CritterType.Unknown]: 'bg-type-unknown',
};

export function getCritterTypeFill(type: CritterType | null | undefined): string {
  return critterTypeFills[type ?? CritterType.Unknown];
}


export function getCritterTypeIcon(type: CritterType | null | undefined): string {
  return critterTypeIcons[type ?? CritterType.Unknown];
}

export function getCritterTypeStyle(type: CritterType | null | undefined): string {
    return critterTypeStyles[type ?? CritterType.Unknown];
}

export const getCritterImageUrl = (critterName: string | null | undefined): string => {
  const toTitleCase = (str: string): string =>
    str.replace(/\w\S*/g, (txt) =>
      txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase()
    );

  const formattedName = toTitleCase(critterName ?? 'Unknown').replace(/\s/g, '');
  return `/critters/${formattedName}.jpeg`;
};

const effectTypeStyles: Record<string, string> = {
  DamageEffect: 'bg-danger-soft text-danger-ink border-danger/35',
  DamageOverTimeEffect: 'bg-purple-100 text-purple-800 border-purple-300',
  SkipTurnEffect: 'bg-blue-100 text-blue-800 border-blue-300',
  BuffEffect: 'bg-accent-soft text-accent-ink border-accent/40',
  DebuffEffect: 'bg-yellow-100 text-yellow-800 border-yellow-300',
  UnknownEffect: 'bg-surface-sunk text-ink border-line-strong',
};

export const getEffectStyle = (effect: EffectUnion | null | undefined): string => {
  if (!effect || !effect.__typename) return effectTypeStyles.UnknownEffect;
  return effectTypeStyles[effect.__typename] ?? effectTypeStyles.UnknownEffect;
};

const connectionStatusStyleMap: Record<ConnectionStatus, { text: string; color: string }> = {
  [ConnectionStatus.CONNECTED]: { text: 'Online', color: 'bg-green-500' },
  [ConnectionStatus.CONNECTING]: { text: 'Connecting...', color: 'bg-yellow-400' },
  [ConnectionStatus.DISCONNECTED]: { text: 'Offline', color: 'bg-gray-400' },
  [ConnectionStatus.ERROR]: { text: 'Error', color: 'bg-red-500' },
};

export function getConnectionStatusStyle(status: ConnectionStatus | null | undefined): { text: string; color: string } {
  return connectionStatusStyleMap[status ?? ConnectionStatus.DISCONNECTED];
}

export const formatDuration = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
};

export const formatTimestamp = (timestamp: string | null | undefined): string => {
  if (!timestamp) return 'Unknown date';
  const date = new Date(timestamp);
  return date.toLocaleString();
};

const battleEffectMeta: Record<BattleEffect['_type'], { label: string; icon: string; style: string }> = {
  DamageEffect: { label: 'Struck', icon: '💥', style: 'bg-danger-soft text-danger-ink border-danger/35' },
  DamageOverTimeEffect: { label: 'Wounded', icon: '☠️', style: 'bg-purple-100 text-purple-800 border-purple-300' },
  SkipTurnEffect: { label: 'Stunned', icon: '💫', style: 'bg-blue-100 text-blue-800 border-blue-300' },
};

export function getBattleEffectMeta(effect: BattleEffect) {
  return battleEffectMeta[effect._type];
}

/** Persistent effects carry a countdown; an instant hit does not. */
export function getBattleEffectDuration(effect: BattleEffect): number | null {
  return effect._type === 'DamageEffect' ? null : effect.duration;
}

/**
 * What an effect does, in the fewest words that still let a move be compared to
 * the one beside it. An ability can carry more than one, and the second is
 * usually the reason to pick it.
 */
export function describeBattleEffect(effect: BattleEffect): string {
  if (effect._type === 'DamageEffect') return `${effect.damage} damage`;
  if (effect._type === 'DamageOverTimeEffect') return `${effect.damagePerTurn} a turn for ${effect.duration}`;
  return 'costs a turn';
}

export function getAbilityPower(effect: BattleEffect | undefined): number | null {
  if (!effect) return null;
  if (effect._type === 'DamageEffect') return effect.damage;
  if (effect._type === 'DamageOverTimeEffect') return effect.damagePerTurn;
  return null;
}

export function getHealthTone(healthPercentage: number): string {
  if (healthPercentage > 50) return 'bg-vital';
  if (healthPercentage > 20) return 'bg-warn';
  return 'bg-ruby';
}

export function getEffectivenessLabel(effectiveness: number): string | null {
  if (effectiveness > 1) return "Super effective!";
  if (effectiveness < 1) return "Not very effective...";
  return null;
}

export type BattleLogTone = 'damage' | 'faint' | 'switch' | 'status' | 'system';

const battleLogToneStyles: Record<BattleLogTone, string> = {
  damage: 'bg-danger-soft border-l-4 border-danger/35 text-ink',
  faint: 'bg-ink text-surface',
  switch: 'bg-accent-soft border-l-4 border-accent/40 text-ink',
  status: 'bg-purple-50 border-l-4 border-purple-300 text-ink',
  system: 'bg-surface-sunk text-ink',
};

/**
 * The log arrives as prose, so the styling is matched off the phrases gamelogic
 * writes. Anything unrecognised falls back to the plain system row.
 */
export function classifyBattleLog(message: string): BattleLogTone {
  if (message.includes('fainted!') || message.includes('wins the battle!') || message.includes('forfeited')) return 'faint';
  if (message.includes('switched from') || message.includes('is sent out!')) return 'switch';
  if (message.includes('afflicted with') || message.includes('no longer affected') || message.includes('skipping')) return 'status';
  if (message.includes('damage')) return 'damage';
  return 'system';
}

export function getBattleLogStyle(message: string): string {
  return battleLogToneStyles[classifyBattleLog(message)];
}
