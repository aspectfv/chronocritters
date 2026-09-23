import type { CritterData } from '@features/profile/types';
import { getCritterTypeStyle, getEffectStyle } from '@utils/utils';
import { CritterPortrait } from '@components/ui/CritterPortrait';

export const CritterDetails = ({ critter }: { critter: CritterData | null }) => {
  if (!critter) {
    return (
      <div className="bg-surface rounded-xl shadow-sm border border-line p-6 h-full flex flex-col items-center justify-center text-center">
        <div className="w-24 h-24 bg-surface-sunk rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-ink-faint" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        </div>
        <h3 className="font-semibold text-ink text-xl">Select a Critter</h3>
        <p className="text-ink-muted">Select a critter from the list to view its details.</p>
      </div>
    );
  }

  const xpPercentage = Math.min(100, ((critter.baseStats?.experience ?? 0) / (critter.baseStats?.expToNextLevel ?? 1)) * 100);

  return (
    <div className="bg-surface rounded-xl shadow-sm border border-line p-6 h-full">
      <h3 className="font-semibold text-lg text-ink mb-6">{critter.name} Details</h3>
      
      <div className="text-center mb-6">
        <CritterPortrait name={critter.name} size="lg" className="mb-4" />
        <h3 className="font-bold text-2xl text-ink">{critter.name}</h3>
        <span className={`${getCritterTypeStyle(critter.type)} text-xs font-semibold px-3 py-1 rounded-full`}>{critter.type}</span>
        {critter.description && (
          <p className="mt-2 text-ink-muted text-sm">{critter.description}</p>
        )}
      </div>

      <div className="mb-8">
        <div className="flex justify-between text-sm mb-1">
          <span className="font-semibold text-ink">Level {critter.baseStats?.level ?? 1}</span>
          <span className="text-ink-muted">{critter.baseStats?.experience ?? 0} / {critter.baseStats?.expToNextLevel ?? 0} XP</span>
        </div>
        <div className="w-full bg-line rounded-full h-2">
          <div className="bg-accent h-2 rounded-full" style={{width: `${xpPercentage}%`}}></div>
        </div>
      </div>

      <h4 className="font-semibold text-ink mb-4">Base Stats</h4>
      <div className="mb-8 grid grid-cols-1 gap-2 sm:grid-cols-3 sm:gap-3">
        <div className="bg-surface-sunk p-3 rounded-lg flex items-center gap-2 font-medium text-ink">
          <span className="text-danger">♡</span> HP: {critter.baseStats?.health ?? 0}
        </div>
        <div className="bg-surface-sunk p-3 rounded-lg flex items-center gap-2 font-medium text-ink">
          <span className="text-warn">⚔</span> ATK: {critter.baseStats?.attack ?? 0}
        </div>
        <div className="bg-surface-sunk p-3 rounded-lg flex items-center gap-2 font-medium text-ink">
          <span className="text-type-water">🛡</span> DEF: {critter.baseStats?.defense ?? 0}
        </div>
      </div>

      <h4 className="font-semibold text-ink mb-4">Abilities</h4>
      <div className="space-y-3">
        {critter.abilities?.map(ability => (
          <div key={ability?.id} className="p-3 rounded-lg bg-surface-sunk border border-line">
            <p className="font-semibold text-ink">{ability?.name}</p>
            {ability?.description && (
              <p className="text-sm text-ink-muted mt-1">{ability.description}</p>
            )}
            <div className="flex flex-wrap gap-2 mt-2">
              {ability?.effects?.map((effect, index) => (
                <span key={index} className={`text-xs font-semibold px-2 py-1 rounded-full border ${getEffectStyle(effect)}`}>
                  {effect?.description}
                </span>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};