import { CritterType } from "@store/battle/types";

export const critterTypeIcons: Record<CritterType, string> = {
  [CritterType.FIRE]: '🔥',
  [CritterType.WATER]: '💧',
  [CritterType.ELECTRIC]: '⚡',
  [CritterType.GRASS]: '🌿',
  [CritterType.UNKNOWN]: '❓',
};

export const getCritterImageUrl = (critterName: string): string => {
  if (!critterName) {
    return '/src/assets/critters/Unknown.jpeg';
  }
  const formattedName = critterName.toLowerCase().replace(/\s+/g, '');
  return `/src/assets/critters/${formattedName}.jpeg`;
};