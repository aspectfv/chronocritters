import { CritterType } from "@store/battle/types";

export const critterTypeIcons: Record<CritterType, string> = {
  [CritterType.FIRE]: '🔥',
  [CritterType.WATER]: '💧',
  [CritterType.ELECTRIC]: '⚡',
  [CritterType.GRASS]: '🌿',
  [CritterType.UNKNOWN]: '❓',
};

export const getCritterImageUrl = (critterName: string): string => {
  const toTitleCase = (str: string): string =>
    str.replace(/\w\S*/g, (txt) =>
      txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
    );
    
  const formattedName = toTitleCase(critterName).replace(/\s+/g, '');
  return `/src/assets/critters/${formattedName}.jpeg`;
};