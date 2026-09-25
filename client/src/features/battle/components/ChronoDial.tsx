import type { ChronoDialProps } from '@features/battle/types';

/**
 * The turn clock as an escapement dial. A depleting ring plus a sweep hand,
 * which is the one element that earns the game the name it has been carrying.
 */
export function ChronoDial({ timeRemaining, turnDuration }: ChronoDialProps) {
  const fraction = turnDuration > 0 ? Math.max(0, Math.min(1, timeRemaining / turnDuration)) : 0;
  const isCritical = timeRemaining <= 5;
  const isLow = timeRemaining <= 10;

  const radius = 26;
  const circumference = 2 * Math.PI * radius;
  const stroke = isCritical ? 'var(--color-danger)' : isLow ? 'var(--color-warn)' : 'var(--color-brass)';
  const handAngle = (1 - fraction) * 360;

  return (
    <div
      className={`relative grid h-20 w-20 place-items-center rounded-full border-2 bg-arena-deep shadow-raised ${isCritical ? 'border-danger/60' : 'border-brass/50'}`}
      role="timer"
      aria-label={`${timeRemaining} seconds left this turn`}
    >
      <svg className="absolute inset-0 h-full w-full -rotate-90" viewBox="0 0 64 64" aria-hidden="true">
        <circle cx="32" cy="32" r={radius} fill="none" stroke="var(--color-arena-glass)" strokeWidth="4" />
        <circle
          cx="32" cy="32" r={radius} fill="none" stroke={stroke} strokeWidth="4" strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={circumference * (1 - fraction)}
          style={{ transition: 'stroke-dashoffset 1s linear, stroke 300ms ease' }}
        />
      </svg>

      {/* A marker riding the ring rather than a centre-pinned hand, which at
          34% of the dial's height reached the middle and sat on the digits. */}
      <div
        className="absolute h-full w-full"
        style={{ transform: `rotate(${handAngle}deg)`, transition: 'transform 1s linear' }}
        aria-hidden="true"
      >
        <span className={`absolute left-1/2 top-[7%] h-[13%] w-[3px] -translate-x-1/2 rounded-full ${isCritical ? 'bg-danger' : isLow ? 'bg-warn' : 'bg-brass-ink'}`} />
      </div>

      <span className={`relative z-10 text-xl font-black tabular-nums ${isCritical ? 'animate-pulse text-danger' : isLow ? 'text-warn' : 'text-brass-ink'}`}>
        {timeRemaining}
      </span>
      <span className="absolute inset-0 rounded-full ring-1 ring-inset ring-black/5" aria-hidden="true" />
    </div>
  );
}
