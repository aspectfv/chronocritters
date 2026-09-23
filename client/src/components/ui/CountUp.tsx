import { useEffect, useState } from 'react';

/**
 * Counts a figure up on mount. The results page showed every number at its
 * final value the instant it painted, which threw away the one moment the
 * screen exists for.
 */
export function CountUp({ to, durationMs = 900, delayMs = 0, format }: {
  to: number;
  durationMs?: number;
  delayMs?: number;
  format?: (value: number) => string;
}) {
  const [value, setValue] = useState(0);

  useEffect(() => {
    const reduced = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
    if (reduced || to === 0) { setValue(to); return; }

    let frame = 0;
    const start = performance.now() + delayMs;
    const tick = (now: number) => {
      const elapsed = now - start;
      if (elapsed < 0) { frame = requestAnimationFrame(tick); return; }
      const progress = Math.min(1, elapsed / durationMs);
      // Ease out, so it decelerates into the final figure.
      setValue(Math.round(to * (1 - Math.pow(1 - progress, 3))));
      if (progress < 1) frame = requestAnimationFrame(tick);
    };
    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [to, durationMs, delayMs]);

  return <>{format ? format(value) : value}</>;
}
