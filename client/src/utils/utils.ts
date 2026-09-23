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
  [CritterType.Fire]: 'bg-red-100 text-red-800',
  [CritterType.Water]: 'bg-blue-100 text-blue-800',
  [CritterType.Grass]: 'bg-green-100 text-green-800',
  [CritterType.Electric]: 'bg-yellow-100 text-yellow-800',
  [CritterType.Metal]: 'bg-gray-100 text-gray-800',
  [CritterType.Toxic]: 'bg-purple-100 text-gray-800',
  [CritterType.Kinetic]: 'bg-indigo-100 text-indigo-800',
  [CritterType.Unknown]: 'bg-gray-100 text-gray-800',
};

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
  DamageEffect: 'bg-red-100 text-red-800 border-red-300',
  DamageOverTimeEffect: 'bg-purple-100 text-purple-800 border-purple-300',
  SkipTurnEffect: 'bg-blue-100 text-blue-800 border-blue-300',
  BuffEffect: 'bg-green-100 text-green-800 border-green-300',
  DebuffEffect: 'bg-yellow-100 text-yellow-800 border-yellow-300',
  UnknownEffect: 'bg-gray-100 text-gray-800 border-gray-300',
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
  DamageEffect: { label: 'Struck', icon: '💥', style: 'bg-red-100 text-red-800 border-red-300' },
  DamageOverTimeEffect: { label: 'Poisoned', icon: '☠️', style: 'bg-purple-100 text-purple-800 border-purple-300' },
  SkipTurnEffect: { label: 'Stunned', icon: '💫', style: 'bg-blue-100 text-blue-800 border-blue-300' },
};

export function getBattleEffectMeta(effect: BattleEffect) {
  return battleEffectMeta[effect._type];
}

/** Persistent effects carry a countdown; an instant hit does not. */
export function getBattleEffectDuration(effect: BattleEffect): number | null {
  return effect._type === 'DamageEffect' ? null : effect.duration;
}

export function getAbilityPower(effect: BattleEffect | undefined): number | null {
  if (!effect) return null;
  if (effect._type === 'DamageEffect') return effect.damage;
  if (effect._type === 'DamageOverTimeEffect') return effect.damagePerTurn;
  return null;
}

export function getHealthTone(healthPercentage: number): string {
  if (healthPercentage > 50) return 'bg-green-500';
  if (healthPercentage > 20) return 'bg-amber-400';
  return 'bg-red-500';
}

export function getEffectivenessLabel(effectiveness: number): string | null {
  if (effectiveness > 1) return "Super effective!";
  if (effectiveness < 1) return "Not very effective...";
  return null;
}

export type BattleLogTone = 'damage' | 'faint' | 'switch' | 'status' | 'system';

const battleLogToneStyles: Record<BattleLogTone, string> = {
  damage: 'bg-red-50 border-l-4 border-red-300 text-gray-800',
  faint: 'bg-gray-800 text-gray-100',
  switch: 'bg-emerald-50 border-l-4 border-emerald-300 text-gray-800',
  status: 'bg-purple-50 border-l-4 border-purple-300 text-gray-800',
  system: 'bg-cyan-50/60 text-gray-800',
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
