import type { CritterListProps } from '@features/profile/types';
import { CritterPortrait } from '@components/ui/CritterPortrait';

export const CritterList = ({ roster, selectedCritter, onCritterSelect }: CritterListProps) => {
  return (
    <div className="bg-surface rounded-xl shadow-sm border border-line p-6 h-full">
      <h3 className="font-semibold text-lg text-ink mb-4">My Critters ({roster.length})</h3>
      {roster.length === 0 ? (
        <p className="text-sm text-ink-muted">You have no critters yet.</p>
      ) : (
        <div className="space-y-3">
          {roster.map((critter) => (
            <button
              key={critter.id}
              type="button"
              aria-pressed={selectedCritter?.id === critter.id}
              onClick={() => onCritterSelect(critter)}
              className={`w-full p-4 rounded-lg border-2 text-left transition-colors flex items-center gap-4 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent-ring focus-visible:ring-offset-2 ${
                selectedCritter?.id === critter.id
                  ? 'bg-accent-soft border-accent'
                  : 'bg-surface border-line hover:border-accent/40'
              }`}
            >
              <CritterPortrait name={critter.name} size="sm" />
              <div className="flex-grow min-w-0">
                <p className="font-bold text-ink truncate">{critter.name}</p>
                <div className="flex flex-wrap items-center gap-2 text-sm text-ink-muted">
                  <span>{critter.type}</span>
                  <span className="bg-line px-2 py-0.5 rounded-full text-xs font-semibold">Level {critter.baseStats?.level ?? 1}</span>
                </div>
              </div>
              {selectedCritter?.id === critter.id && (
                <span className="sr-only">Selected</span>
              )}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};
