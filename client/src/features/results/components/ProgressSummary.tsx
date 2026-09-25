import { useEffect, useState } from 'react';
import { TrendingUp } from 'lucide-react';
import { Surface } from '@components/ui/Surface';
import type { ProgressBarProps, ProgressSummaryProps } from '@features/results/types';

const FILL_MS = 1000;

const ProgressBar = ({ name, finalStats, expGained, delayMs = 0 }: ProgressBarProps & { delayMs?: number }) => {
  const finalExp = finalStats?.experience ?? 0;
  const finalLevel = finalStats?.level ?? 1;
  const expToNextLevel = finalStats?.expToNextLevel ?? 0;
  const startExp = Math.max(0, finalExp - expGained);

  // The bar used a CSS width transition while the label jumped in one step, so
  // the two disagreed for a full second. Both now read the same animated value.
  const [shownExp, setShownExp] = useState(startExp);

  useEffect(() => {
    setShownExp(startExp);
    if (expGained === 0) return;

    const reduced = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
    if (reduced) { setShownExp(finalExp); return; }

    let frame = 0;
    const begin = performance.now() + delayMs;
    const tick = (now: number) => {
      const elapsed = now - begin;
      if (elapsed < 0) { frame = requestAnimationFrame(tick); return; }
      const progress = Math.min(1, elapsed / FILL_MS);
      setShownExp(startExp + (finalExp - startExp) * (1 - Math.pow(1 - progress, 3)));
      if (progress < 1) frame = requestAnimationFrame(tick);
    };
    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [startExp, finalExp, expGained, delayMs]);

  const percentage = expToNextLevel > 0 ? Math.min(100, (shownExp / expToNextLevel) * 100) : 0;

  return (
    <div>
      <div className="mb-1 flex items-baseline justify-between gap-2 text-sm">
        <span className="min-w-0 truncate font-semibold text-ink">{name}</span>
        <span className="shrink-0 text-xs text-ink-muted">Lv {finalLevel}</span>
      </div>
      <div
        className="h-2.5 w-full overflow-hidden rounded-full bg-surface-sunk"
        role="progressbar"
        aria-label={`${name} experience`}
        aria-valuenow={Math.round(shownExp)}
        aria-valuemin={0}
        aria-valuemax={expToNextLevel}
      >
        <div className="h-full rounded-full bg-accent" style={{ width: `${percentage}%` }} />
      </div>
      <div className="mt-1 flex items-baseline justify-between text-xs">
        <span className="tabular-nums text-ink-faint">{Math.floor(shownExp)}/{expToNextLevel} XP</span>
        {expGained > 0 && <span className="font-semibold text-accent-ink">+{expGained}</span>}
      </div>
    </div>
  );
};

export const ProgressSummary = ({ player, critters, expGained, critterExpGained }: ProgressSummaryProps) => (
  <Surface className="flex h-full flex-col">
    <h2 className="mb-4 flex items-center gap-2 text-sm font-medium text-ink-muted">
      <TrendingUp className="h-4 w-4" aria-hidden="true" />
      Progress
    </h2>

    <ProgressBar name={player?.username ?? 'Trainer'} finalStats={player?.stats} expGained={expGained} delayMs={300} />

    {critters && critters.length > 0 && (
      <>
        <hr className="my-4 border-line" />
        <p className="mb-3 text-xs text-ink-faint">Critters</p>
        <div className="space-y-3">
          {critters.map((critter, index) => (
            <ProgressBar
              key={critter?.id}
              name={critter?.name ?? 'Unknown'}
              finalStats={critter?.baseStats}
              expGained={critterExpGained[critter?.id ?? ''] ?? 0}
              delayMs={450 + index * 120}
            />
          ))}
        </div>
      </>
    )}
  </Surface>
);
