import { useEffect, useState } from 'react';
import type { CritterDisplayCardProps } from '@features/battle/types';
import { StatusEffectChips } from '@features/battle/components/StatusEffectChips';
import { getCritterImageUrl, getCritterTypeStyle, getEffectivenessLabel, getHealthTone } from '@utils/utils';

const HIT_ANIMATION_MS = 1100;

export function CritterDisplayCard({
  playerName,
  critter,
  mirrored = false,
  hitTurn,
  hitDamage = 0,
  hitEffectiveness = 1,
}: CritterDisplayCardProps) {
  const [isHit, setIsHit] = useState(false);

  // Keyed off the turn number so the same damage figure landing twice still
  // replays, and so a re-render on unrelated state does not.
  useEffect(() => {
    if (hitTurn === undefined) return;

    setIsHit(true);
    const settle = setTimeout(() => setIsHit(false), HIT_ANIMATION_MS);
    return () => clearTimeout(settle);
  }, [hitTurn]);

  const healthPercentage = Math.max(0, (critter.stats.currentHp / critter.stats.maxHp) * 100);
  const effectivenessLabel = getEffectivenessLabel(hitEffectiveness);

  return (
    <div
      className={`relative bg-white rounded-xl shadow-sm border border-gray-200 p-4 ${isHit ? 'animate-critter-hit border-red-300' : ''}`}
    >
      {isHit && hitDamage > 0 && (
        <div className="pointer-events-none absolute left-1/2 top-24 z-10 animate-damage-float text-center">
          <span className={`block font-extrabold drop-shadow ${hitEffectiveness > 1 ? 'text-4xl text-red-600' : 'text-3xl text-red-500'}`}>
            -{hitDamage}
          </span>
          {effectivenessLabel && (
            <span className="block text-xs font-bold uppercase tracking-wide text-red-700">{effectivenessLabel}</span>
          )}
        </div>
      )}

      <div className="flex justify-between items-center text-sm mb-3">
        <span className="font-bold text-lg text-green-700">{playerName}</span>
        <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${getCritterTypeStyle(critter.type)}`}>
          {critter.type}
        </span>
      </div>
      <div className="text-center">
        <div className="mx-auto flex items-center justify-center mb-2">
          <img
            src={getCritterImageUrl(critter.name)}
            alt={critter.name}
            className={`w-28 h-28 object-cover rounded-md transition-opacity duration-300 ${mirrored ? 'scale-x-[-1]' : ''} ${critter.fainted ? 'opacity-40 grayscale' : ''}`}
            onError={e => {
              const target = e.target as HTMLImageElement;
              target.src = getCritterImageUrl('Unknown');
            }}
          />
        </div>
        <h2 className="text-2xl font-bold text-gray-800">{critter.name}</h2>
      </div>

      <div className="mt-2 min-h-6">
        <StatusEffectChips effects={critter.activeStatusEffects} />
      </div>

      <div className="my-4">
        <div className="flex justify-between items-center text-xs text-gray-600 mb-1">
          <span className="flex items-center gap-1 font-medium">
            <span className="text-red-500">♡</span> HP
          </span>
          <span>{critter.stats.currentHp}/{critter.stats.maxHp}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className={`h-2 rounded-full transition-[width,background-color] duration-500 ease-out ${getHealthTone(healthPercentage)}`}
            style={{ width: `${healthPercentage}%` }}
          ></div>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-2 text-center">
        <div className="bg-gray-100 p-2 rounded-lg">
          <p className="font-bold text-lg text-gray-700">{critter.stats.currentAtk}</p>
          <p className="text-xs text-gray-500">ATK</p>
        </div>
        <div className="bg-gray-100 p-2 rounded-lg">
          <p className="font-bold text-lg text-gray-700">{critter.stats.currentDef}</p>
          <p className="text-xs text-gray-500">DEF</p>
        </div>
      </div>
    </div>
  );
}
