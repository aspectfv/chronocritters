import type { ForcedSwitchPanelProps } from '@features/battle/types';
import { getCritterImageUrl, getCritterTypeStyle, getHealthTone } from '@utils/utils';

export function ForcedSwitchPanel({ team, onCritterClick, disabled }: ForcedSwitchPanelProps) {
  const available = team
    .map((critter, index) => ({ critter, index }))
    .filter(({ critter }) => critter.stats.currentHp > 0);

  return (
    <div className="fixed inset-0 z-30 flex items-end justify-center bg-gray-900/50 p-4 sm:items-center">
      <div className="w-full max-w-lg rounded-xl bg-white p-6 shadow-xl">
        <h2 className="text-center text-xl font-bold text-gray-800">Your critter fainted</h2>
        <p className="mt-1 text-center text-sm text-gray-500">
          Choose who goes out next. This one is free — you still get your turn.
        </p>

        <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2">
          {available.map(({ critter, index }) => {
            const healthPercentage = Math.max(0, (critter.stats.currentHp / critter.stats.maxHp) * 100);

            return (
              <button
                key={critter.id}
                type="button"
                disabled={disabled}
                onClick={() => onCritterClick(index)}
                className="flex items-center gap-3 rounded-lg border-2 border-gray-200 p-3 text-left transition-all hover:border-green-400 focus:outline-none focus:ring-2 focus:ring-green-400 disabled:opacity-50"
              >
                <img
                  src={getCritterImageUrl(critter.name)}
                  alt={critter.name}
                  className="h-14 w-14 shrink-0 rounded-md object-cover"
                  onError={e => {
                    const target = e.target as HTMLImageElement;
                    target.src = getCritterImageUrl('Unknown');
                  }}
                />
                <div className="min-w-0 flex-1">
                  <div className="flex items-center justify-between gap-2">
                    <span className="truncate font-bold text-gray-800">{critter.name}</span>
                    <span className={`shrink-0 rounded-full border px-2 py-0.5 text-xs font-semibold ${getCritterTypeStyle(critter.type)}`}>
                      {critter.type}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-gray-500">{critter.stats.currentHp}/{critter.stats.maxHp} HP</p>
                  <div className="mt-1 h-1.5 w-full rounded-full bg-gray-200">
                    <div className={`h-1.5 rounded-full ${getHealthTone(healthPercentage)}`} style={{ width: `${healthPercentage}%` }}></div>
                  </div>
                </div>
              </button>
            );
          })}
        </div>

        <p className="mt-4 text-center text-xs text-gray-400">
          Run the clock down and the next critter in your roster is sent out for you.
        </p>
      </div>
    </div>
  );
}
