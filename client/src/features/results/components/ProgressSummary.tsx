import { useEffect, useState } from 'react';
import { TrendingUp } from 'lucide-react';
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
        <span className="min-w-0 truncate font-bold text-arena-ink">{name}</span>
        <span className="numeral shrink-0 text-xs text-brass-ink">Lv {finalLevel}</span>
      </div>
      <div
        className="well h-3.5 w-full overflow-hidden rounded-sm"
        role="progressbar"
        aria-label={`${name} experience`}
        aria-valuenow={Math.round(shownExp)}
        aria-valuemin={0}
        aria-valuemax={expToNextLevel}
      >
        <div className="hp-fill h-full bg-brass" style={{ width: `${percentage}%` }} />
      </div>
      <div className="mt-1 flex items-baseline justify-between text-xs">
        <span className="numeral text-arena-ink-muted">{Math.floor(shownExp)}/{expToNextLevel}</span>
        {expGained > 0 && <span className="numeral text-vital">+{expGained}</span>}
      </div>
    </div>
  );
};

export const ProgressSummary = ({ player, critters, expGained, critterExpGained }: ProgressSummaryProps) => (
  <div className="panel flex h-full flex-col rounded-lg bg-arena-deep p-4">
    <h2 className="mb-3 flex items-center gap-2 text-sm font-bold text-arena-ink">
      <TrendingUp className="h-4 w-4 text-brass-ink" aria-hidden="true" />
      Progress
    </h2>

    <ProgressBar name={player?.username ?? 'Trainer'} finalStats={player?.stats} expGained={expGained} delayMs={300} />

    {critters && critters.length > 0 && (
      <>
        <hr className="my-3.5 border-t-2 border-outline/25" />
        <p className="mb-2.5 text-xs font-bold text-arena-ink-muted">Your critters</p>
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
  </div>
);
