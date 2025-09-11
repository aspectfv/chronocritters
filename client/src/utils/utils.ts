import { MatchMakingStatus } from "@features/menu/types";
import { ConnectionStatus } from "@store/lobby/types";
import { CritterType, EffectType, type DamageEffect, type DamageOverTimeEffect, type EffectUnion, type SkipTurnEffect } from "src/gql/graphql";

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

export function getCritterTypeIcon(type: CritterType): string {
  return critterTypeIcons[type] ?? critterTypeIcons[CritterType.Unknown];
}

const critterTypeStyles: Record<CritterType, string> = {
  [CritterType.Fire]: 'bg-red-100 text-red-800',
  [CritterType.Water]: 'bg-blue-100 text-blue-800',
  [CritterType.Grass]: 'bg-green-100 text-green-800',
  [CritterType.Electric]: 'bg-yellow-100 text-yellow-800',
  [CritterType.Metal]: 'bg-gray-100 text-gray-800',
  [CritterType.Toxic]: 'bg-purple-100 text-gray-800',
  [CritterType.Kinetic]: 'bg-indigo-100 text-indigo-800',
  [CritterType.Unknown]: 'bg-gray-100 text-gray-800',
};

export function getCritterTypeStyle(type: CritterType): string {
    return critterTypeStyles[type] ?? critterTypeStyles[CritterType.Unknown];
  }

export const getCritterImageUrl = (critterName: string): string => {
  const toTitleCase = (str: string): string =>
    str.replace(/\w\S*/g, (txt) =>
      txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase()
    );

  const formattedName = toTitleCase(critterName).replace(/\s/g, '');
  return `/src/assets/critters/${formattedName}.jpeg`;
};

const effectDescriptionMap: Record<EffectType, (effect: EffectUnion) => string> = {
  [EffectType.Damage]: (effect) =>
    `Deals ${(effect as DamageEffect).damage} damage.`,
  [EffectType.DamageOverTime]: (effect) =>
    `Deals ${(effect as DamageOverTimeEffect).damagePerTurn} damage for ${(effect as DamageOverTimeEffect).duration} turns.`,
  [EffectType.SkipTurn]: (effect) =>
    `Skips target's turn for ${(effect as SkipTurnEffect).duration} turns.`,
  [EffectType.Buff]: () => 'Buff effect.',
  [EffectType.Debuff]: () => 'Debuff effect.',
  [EffectType.Unknown]: () => 'A mysterious effect.',
};

export const getEffectDescription = (effect: EffectUnion): string => {
  const renderer = effectDescriptionMap[effect.type] ?? effectDescriptionMap[EffectType.Unknown];
  return renderer(effect);
};

const connectionStatusStyleMap: Record<ConnectionStatus, { text: string; color: string }> = {
  [ConnectionStatus.CONNECTED]: { text: 'Online', color: 'bg-green-500' },
  [ConnectionStatus.CONNECTING]: { text: 'Connecting...', color: 'bg-yellow-400' },
  [ConnectionStatus.DISCONNECTED]: { text: 'Offline', color: 'bg-gray-400' },
  [ConnectionStatus.ERROR]: { text: 'Error', color: 'bg-red-500' },
};

export function getConnectionStatusStyle(status: ConnectionStatus): { text: string; color: string } {
  return connectionStatusStyleMap[status] ?? connectionStatusStyleMap[ConnectionStatus.DISCONNECTED];
}

const buttonStateMap: Record<ConnectionStatus, { text: string; disabled: boolean }> = {
  [ConnectionStatus.CONNECTING]: { text: 'Connecting to Lobby...', disabled: true },
  [ConnectionStatus.ERROR]: { text: 'Lobby Offline', disabled: true },
  [ConnectionStatus.DISCONNECTED]: { text: 'Lobby Offline', disabled: true },
  [ConnectionStatus.CONNECTED]: { text: 'Find Match', disabled: false },
};

export const getButtonState = (connectionStatus: ConnectionStatus, matchmakingStatus: MatchMakingStatus) => {
  if (connectionStatus === ConnectionStatus.CONNECTED && matchmakingStatus === MatchMakingStatus.SEARCHING) {
    return { text: 'Searching for Opponent...', disabled: true };
  }
  return buttonStateMap[connectionStatus] || { text: 'Connecting...', disabled: true };
};