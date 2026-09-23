import { Trophy, Flag } from 'lucide-react';
import type { ResultsHeaderProps } from '@features/results/types';

/**
 * The single most loaded moment in the app, previously a static block with no
 * entrance at all. It now lands rather than appears.
 */
export function ResultsHeader({ result, opponentName }: ResultsHeaderProps) {
  const isVictory = result === 'victory';
  const Icon = isVictory ? Trophy : Flag;

  return (
    <div
      className={`animate-turn-claim relative overflow-hidden rounded-card border px-6 py-10 text-center ${
        isVictory ? 'border-accent/30 bg-accent-soft' : 'border-line bg-surface-sunk'
      }`}
    >
      {/* Rays behind the trophy, so a win reads differently from a loss at a glance. */}
      {isVictory && (
        <div
          className="pointer-events-none absolute inset-0 opacity-60"
          aria-hidden="true"
          style={{
            background:
              'radial-gradient(60% 60% at 50% 30%, color-mix(in oklab, var(--color-accent) 22%, transparent) 0%, transparent 70%)',
          }}
        />
      )}

      <div className="relative">
        <Icon
          className={`mx-auto h-14 w-14 ${isVictory ? 'text-accent' : 'text-ink-faint'}`}
          aria-hidden="true"
          strokeWidth={1.5}
        />
        <h1 className={`mt-3 text-5xl font-black tracking-tight ${isVictory ? 'text-accent-ink' : 'text-ink'}`}>
          {isVictory ? 'Victory' : 'Defeat'}
        </h1>
        <p className="mt-2 break-words text-ink-muted">
          {isVictory ? `You defeated ${opponentName}` : `${opponentName} defeated you`}
        </p>
      </div>
    </div>
  );
}
