import type { TeamTrackProps } from '@features/battle/types';
import { getCritterTypeFill } from '@utils/utils';

/**
 * Six-at-a-glance roster indicators, the genre's party dots. Yours are
 * interactive when a switch is legal; the opponent's are read-only.
 */
export function TeamTrack({ team, activeCritterId, align, canSwitch = false, onCritterClick }: TeamTrackProps) {
  return (
    <div className={`flex items-center gap-1.5 ${align === 'right' ? 'justify-end' : 'justify-start'}`}>
      {team.map((critter, index) => {
        const isActive = critter.id === activeCritterId;
        const isDown = critter.stats.currentHp <= 0;
        const selectable = canSwitch && !isDown && !isActive;

        const dot = (
          <span
            className={`block h-3.5 w-3.5 rounded-full border-2 transition-all ${
              isDown
                ? 'border-arena-glass bg-transparent'
                : `${getCritterTypeFill(critter.type)} ${isActive ? 'border-brass-ink scale-125' : 'border-brass/50'}`
            }`}
          />
        );

        const label = `${critter.name}${isDown ? ', fainted' : ''}${isActive ? ', active' : ''}`;

        return selectable ? (
          <button
            key={critter.id}
            type="button"
            title={`Send out ${critter.name}`}
            aria-label={`Send out ${critter.name}`}
            onClick={() => onCritterClick?.(index)}
            className="rounded-full p-0.5 hover:scale-125 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass"
          >
            {dot}
          </button>
        ) : (
          <span key={critter.id} className="p-0.5" title={label} aria-label={label} role="img">
            {dot}
          </span>
        );
      })}
    </div>
  );
}
