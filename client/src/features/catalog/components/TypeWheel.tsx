import type { TypeWheelProps } from '@features/catalog/types';
import { getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';

const SIZE = 300;
const CENTRE = SIZE / 2;
const ORBIT = 96;
const NODE = 30;

function nodePosition(index: number, count: number) {
  const angle = (index / count) * 2 * Math.PI - Math.PI / 2;
  return { x: CENTRE + ORBIT * Math.cos(angle), y: CENTRE + ORBIT * Math.sin(angle) };
}

/**
 * What beats what, drawn as a ring rather than listed as a grid.
 *
 * The edges come from the server's own chart, so this cannot drift from the
 * damage the engine actually deals. Types are spaced evenly around the circle
 * instead of being placed by hand, which is what keeps a fourth type from
 * breaking the drawing.
 */
export function TypeWheel({ advantages, types }: TypeWheelProps) {
  if (types.length < 2) return null;

  // The chart covers types nothing has been built for yet. Drawing those would
  // fill the ring with matchups no critter in the game can reach.
  const edges = advantages.filter(
    (edge) => types.includes(edge.attacker) && types.includes(edge.defender),
  );
  const positions = new Map(types.map((type, index) => [type, nodePosition(index, types.length)]));

  return (
    <figure className="panel rounded-lg bg-arena-deep p-4">
      <figcaption className="mb-1 text-sm font-bold text-arena-ink">What beats what</figcaption>
      <p className="mb-3 text-xs text-arena-ink-muted">
        An arrow points at what its type is strong against. Following it deals half again as much
        damage; going against it deals half.
      </p>

      <div className="relative mx-auto w-full max-w-[300px]" style={{ aspectRatio: '1 / 1' }}>
        <svg viewBox={`0 0 ${SIZE} ${SIZE}`} className="absolute inset-0 h-full w-full" aria-hidden="true">
          <defs>
            <marker id="type-arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="5" markerHeight="5" orient="auto-start-reverse">
              <path d="M 0 0 L 10 5 L 0 10 z" fill="var(--color-outline)" />
            </marker>
          </defs>

          {edges.map((edge) => {
            const from = positions.get(edge.attacker);
            const to = positions.get(edge.defender);
            if (!from || !to) return null;

            // Stopped short of both nodes so the head lands on the rim rather
            // than under the glyph it is pointing at.
            const dx = to.x - from.x;
            const dy = to.y - from.y;
            const length = Math.hypot(dx, dy) || 1;
            const gap = NODE + 8;

            return (
              <line
                key={`${edge.attacker}-${edge.defender}`}
                x1={from.x + (dx / length) * gap}
                y1={from.y + (dy / length) * gap}
                x2={to.x - (dx / length) * gap}
                y2={to.y - (dy / length) * gap}
                stroke="var(--color-outline)"
                strokeWidth={3}
                strokeLinecap="round"
                markerEnd="url(#type-arrow)"
              />
            );
          })}
        </svg>

        {types.map((type) => {
          const { x, y } = positions.get(type)!;
          return (
            <span
              key={type}
              className="absolute -translate-x-1/2 -translate-y-1/2"
              style={{ left: `${(x / SIZE) * 100}%`, top: `${(y / SIZE) * 100}%` }}
            >
              <span className={`flex h-14 w-14 items-center justify-center rounded-full border-[3px] border-outline text-2xl shadow-[0_4px_0_0_var(--color-outline)] ${getCritterTypeFill(type)}`}>
                <span aria-hidden="true">{getCritterTypeIcon(type)}</span>
              </span>
              <span className="mt-1.5 block text-center text-[11px] font-black text-arena-ink">{type}</span>
            </span>
          );
        })}
      </div>
    </figure>
  );
}
