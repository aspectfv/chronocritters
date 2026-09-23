import { getCritterImageUrl } from '@utils/utils';
import { cn } from '@components/ui/cn';

const sizes = {
  sm: 'h-14 w-14',
  md: 'h-24 w-24',
  lg: 'h-28 w-28',
} as const;

/**
 * The one framing for critter art anywhere outside the arena.
 *
 * Every critter is a 1024x1024 illustration on its own saturated gradient with
 * no alpha channel, so it cannot sit on a surface unframed. A ring turns that
 * gradient into a deliberate glow, matching the arena's containment cells in a
 * light register.
 */
export function CritterPortrait({ name, size = 'md', fainted = false, className }: {
  name: string | null | undefined;
  size?: keyof typeof sizes;
  fainted?: boolean;
  className?: string;
}) {
  return (
    <span className={cn('inline-block shrink-0 rounded-full bg-gradient-to-b from-line-strong to-line p-[2px] shadow-card', className)}>
      <span className="relative block overflow-hidden rounded-full">
        <img
          src={getCritterImageUrl(name)}
          alt={name ?? 'Unknown critter'}
          className={cn('block object-cover', sizes[size], fainted && 'grayscale opacity-40')}
          onError={(e) => {
            (e.target as HTMLImageElement).src = getCritterImageUrl('Unknown');
          }}
        />
        <span className="pointer-events-none absolute inset-0 rounded-full bg-gradient-to-b from-white/20 via-transparent to-black/15" aria-hidden="true" />
        <span className="pointer-events-none absolute inset-0 rounded-full ring-1 ring-inset ring-black/10" aria-hidden="true" />
      </span>
    </span>
  );
}
