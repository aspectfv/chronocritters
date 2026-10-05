import { ConnectionStatus } from "@store/lobby/types";
import type { BattleEffect } from "@store/battle/types";
import { CritterType, Stat, StatusCondition, type EffectUnion } from "@/gql/graphql";

const critterTypeIcons: Record<CritterType, string> = {
  [CritterType.Fire]: '🔥',
  [CritterType.Water]: '💧',
  [CritterType.Electric]: '⚡',
  [CritterType.Grass]: '🌿',
  [CritterType.Metal]: '⛓️',
  [CritterType.Toxic]: '☠️',
  [CritterType.Kinetic]: '🌀',
  [CritterType.Unknown]: '❓',
};

const critterTypeStyles: Record<CritterType, string> = {
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

/**
 * The same effects arrive under two discriminants: the battle socket
 * sends `_type`, GraphQL sends `__typename`. The values are identical, so the
 * helpers below read whichever one is there rather than the catalogue carrying
 * a second copy of every table.
 */
export type AnyEffect = BattleEffect | EffectUnion;

function effectKind(effect: AnyEffect): BattleEffect['_type'] | null {
  const kind = '_type' in effect ? effect._type : effect.__typename;
  return kind ?? null;
}

type EffectMeta = { label: string; icon: string; style: string };

const battleEffectMeta: Record<Exclude<BattleEffect['_type'], 'StatusEffect'>, EffectMeta> = {
  DamageEffect: { label: 'Struck', icon: '💥', style: 'bg-danger-soft text-danger-ink border-danger/35' },
  DamageOverTimeEffect: { label: 'Wounded', icon: '🩹', style: 'bg-purple-100 text-purple-800 border-purple-300' },
  StatModifierEffect: { label: 'Shifted', icon: '📊', style: 'bg-amber-100 text-amber-800 border-amber-300' },
  HealEffect: { label: 'Mended', icon: '💚', style: 'bg-green-100 text-green-800 border-green-300' },
  LifestealEffect: { label: 'Drained', icon: '🩸', style: 'bg-rose-100 text-rose-800 border-rose-300' },
  ShieldEffect: { label: 'Warded', icon: '🛡️', style: 'bg-slate-100 text-slate-800 border-slate-300' },
  CleanseEffect: { label: 'Cleansed', icon: '✨', style: 'bg-cyan-100 text-cyan-800 border-cyan-300' },
};

/** A status reads by its condition, since that is what the player has to answer. */
const statusMeta: Record<StatusCondition, EffectMeta> = {
  [StatusCondition.Burn]: { label: 'Burned', icon: '🔥', style: 'bg-orange-100 text-orange-800 border-orange-300' },
  [StatusCondition.Poison]: { label: 'Poisoned', icon: '☠️', style: 'bg-purple-100 text-purple-800 border-purple-300' },
  [StatusCondition.Paralysis]: { label: 'Paralyzed', icon: '⚡', style: 'bg-yellow-100 text-yellow-800 border-yellow-300' },
  [StatusCondition.Sleep]: { label: 'Asleep', icon: '💤', style: 'bg-blue-100 text-blue-800 border-blue-300' },
};

const statLabels: Record<Stat, string> = {
  [Stat.Attack]: 'ATK',
  [Stat.Defense]: 'DEF',
  [Stat.Speed]: 'SPD',
};

export function getStatLabel(stat: Stat): string {
  return statLabels[stat];
}

export function getBattleEffectMeta(effect: AnyEffect): EffectMeta {
  const kind = effectKind(effect);
  if (kind === 'StatusEffect') return 'condition' in effect ? statusMeta[effect.condition] : battleEffectMeta.DamageEffect;
  return kind ? battleEffectMeta[kind] : battleEffectMeta.DamageEffect;
}

/** Persistent effects carry a countdown; an instant one does not. */
export function getBattleEffectDuration(effect: AnyEffect): number | null {
  return 'duration' in effect ? effect.duration : null;
}

/**
 * What an effect does, in the fewest words that still let a move be compared to
 * the one beside it. An ability can carry more than one, and the second is
 * usually the reason to pick it.
 */
export function describeBattleEffect(effect: AnyEffect): string {
  switch (effectKind(effect)) {
    case 'DamageEffect':
      return 'damage' in effect ? `${effect.damage} damage` : '';
    case 'DamageOverTimeEffect':
      return 'damagePerTurn' in effect ? `${effect.damagePerTurn} a turn for ${effect.duration}` : '';
    case 'StatModifierEffect':
      if (!('stages' in effect)) return '';
      return effect.stages > 0
        ? `own ${getStatLabel(effect.stat)} +${effect.stages}`
        : `foe ${getStatLabel(effect.stat)} ${effect.stages}`;
    case 'HealEffect':
      return 'healPercent' in effect ? `heals ${effect.healPercent}%` : '';
    case 'LifestealEffect':
      return 'drainPercent' in effect ? `${effect.damage} damage, drains ${effect.drainPercent}%` : '';
    case 'ShieldEffect':
      return 'amount' in effect ? `wards ${effect.amount} for ${effect.duration}` : '';
    case 'CleanseEffect':
      return 'clears ailments';
    case 'StatusEffect':
      return 'condition' in effect ? describeStatus(effect.condition, effect.duration, effect.potency) : '';
    default:
      return '';
  }
}

function describeStatus(condition: StatusCondition, duration: number, potency: number): string {
  switch (condition) {
    case StatusCondition.Burn:
      return `burns ${potency} a turn for ${duration}, halves ATK`;
    case StatusCondition.Poison:
      return `poisons ${potency} more each turn for ${duration}`;
    case StatusCondition.Paralysis:
      return `paralyzes for ${duration}, halves SPD`;
    case StatusCondition.Sleep:
      return `sleeps ${duration} turn${duration === 1 ? '' : 's'}`;
  }
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

/**
 * The log arrives as prose, so the styling is matched off the phrases gamelogic
 * writes. Anything unrecognised falls back to the plain system row.
 */
export function classifyBattleLog(message: string): BattleLogTone {
  if (message.includes('fainted!') || message.includes('wins the battle!') || message.includes('forfeited')) return 'faint';
  if (message.includes('switched from') || message.includes('is sent out!')) return 'switch';
  if (message.includes('afflicted with') || message.includes('no longer') || message.includes('fast asleep') || message.includes('has no effect')) return 'status';
  if (message.includes('damage')) return 'damage';
  return 'system';
}

