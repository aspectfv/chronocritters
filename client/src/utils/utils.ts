import { CritterType } from "@store/battle/types";

export const critterTypeIcons: Record<CritterType, string> = {
  [CritterType.FIRE]: '🔥',
  [CritterType.WATER]: '💧',
  [CritterType.ELECTRIC]: '⚡',
  [CritterType.GRASS]: '🌿',
  [CritterType.UNKNOWN]: '❓',
};

const critterTypeStyles: Record<CritterType, string> = {
  [CritterType.FIRE]: 'bg-red-100 text-red-800',
  [CritterType.WATER]: 'bg-blue-100 text-blue-800',
  [CritterType.GRASS]: 'bg-green-100 text-green-800',
  [CritterType.ELECTRIC]: 'bg-yellow-100 text-yellow-800',
  [CritterType.UNKNOWN]: 'bg-gray-100 text-gray-800',
};

export function getCritterTypeStyle(type: CritterType): string {
  return critterTypeStyles[type] ?? critterTypeStyles[CritterType.UNKNOWN];
}

export function getCritterTypeIcon(type: CritterType): string {
  return critterTypeIcons[type] ?? critterTypeIcons[CritterType.UNKNOWN];
}

export const getCritterImageUrl = (critterName: string): string => {
  const toTitleCase = (str: string): string =>
    str.replace(/\w\S*/g, (txt) =>
      txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
    );
    
  const formattedName = toTitleCase(critterName).replace(/\s+/g, '');
  return `/src/assets/critters/${formattedName}.jpeg`;
};