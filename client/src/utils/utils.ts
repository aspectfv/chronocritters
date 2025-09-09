import { MatchMakingStatus } from "@features/menu/types";
import { AbilityType, CritterType } from "@store/battle/types";
import { ConnectionStatus } from "@store/lobby/types";

export const critterTypeIcons: Record<CritterType, string> = {
  [CritterType.FIRE]: '🔥',
  [CritterType.WATER]: '💧',
  [CritterType.ELECTRIC]: '⚡',
  [CritterType.GRASS]: '🌿',
  [CritterType.METAL]: '⛓️',
  [CritterType.UNKNOWN]: '❓',
};

export function getCritterTypeIcon(type: CritterType): string {
  return critterTypeIcons[type] ?? critterTypeIcons[CritterType.UNKNOWN];
}

const critterTypeStyles: Record<CritterType, string> = {
  [CritterType.FIRE]: 'bg-red-100 text-red-800',
  [CritterType.WATER]: 'bg-blue-100 text-blue-800',
  [CritterType.GRASS]: 'bg-green-100 text-green-800',
  [CritterType.ELECTRIC]: 'bg-yellow-100 text-yellow-800',
  [CritterType.METAL]: 'bg-gray-100 text-gray-800',
  [CritterType.UNKNOWN]: 'bg-gray-100 text-gray-800',
};

export function getCritterTypeStyle(type: CritterType): string {
  return critterTypeStyles[type] ?? critterTypeStyles[CritterType.UNKNOWN];
}

const abilityTypeStyle: Record<AbilityType, string> = {
  [AbilityType.ATTACK]: 'bg-red-100 text-red-800',
  [AbilityType.DEFENSE]: 'bg-blue-100 text-blue-800',
  [AbilityType.HEAL]: 'bg-green-100 text-green-800',
  [AbilityType.EFFECT]: 'bg-purple-100 text-purple-800',
  [AbilityType.UNKNOWN]: 'bg-gray-100 text-gray-800',
};

export function getAbilityTypeStyle(type: AbilityType): string {
  return abilityTypeStyle[type] ?? abilityTypeStyle[AbilityType.UNKNOWN];
}

export const getCritterImageUrl = (critterName: string): string => {
  const toTitleCase = (str: string): string =>
    str.replace(/\w\S*/g, (txt) =>
      txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase()
    );

  const formattedName = toTitleCase(critterName).replace(/\s/g, '');
  return `/src/assets/critters/${formattedName}.jpeg`;
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

export const getAbilityDescriptionMap: Record<AbilityType, (power: number) => string> = {
  [AbilityType.ATTACK]: (power: number) => `A powerful strike dealing ${power} damage.`,
  [AbilityType.DEFENSE]: (power: number) => `Boosts defense by ${power} points.`,
  [AbilityType.HEAL]: (power: number) => `Restores ${power} health.`,
  [AbilityType.EFFECT]: () => 'Applies a special effect to the target.',
  [AbilityType.UNKNOWN]: () => 'An ability with a mysterious effect.',
};

export const getAbilityDescription = (ability: { type: AbilityType; power: number }): string => {
  return getAbilityDescriptionMap[ability.type](ability.power) ?? getAbilityDescriptionMap[AbilityType.UNKNOWN](ability.power);
};
