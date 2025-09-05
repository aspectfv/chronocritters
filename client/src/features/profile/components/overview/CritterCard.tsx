import type { CritterCardProps } from '@features/profile/types';
import type { CritterType } from '@store/battle/types';

const typeIcons: Record<CritterType, string> = {
  FIRE: '🔥',
  WATER: '💧',
  ELECTRIC: '⚡',
  GRASS: '🌍',
  UNKNOWN: '❓',
};

export function CritterCard({ name, level, type }: CritterCardProps) {
  return (
    <div className="text-center p-4 bg-green-50/50 rounded-lg border border-green-100">
      <div className="text-4xl mb-2">{typeIcons[type]}</div>
      <p className="font-bold text-gray-800">{name}</p>
      <p className="text-sm text-gray-500">Lv. {level}</p>
    </div>
  );
}