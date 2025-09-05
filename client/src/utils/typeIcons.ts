import type { CritterType } from "@store/battle/types";

export const typeIcons: Record<CritterType, string> = {
  FIRE: '🔥',
  WATER: '💧',
  ELECTRIC: '⚡',
  GRASS: '🌿', // changed from 🌍 to 🌿
  UNKNOWN: '❓',
};