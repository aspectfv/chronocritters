import { Trophy, Flag } from 'lucide-react';
import type { ResultsHeaderProps } from '@features/results/types';

/**
 * The most loaded moment in the app. It stands on the arena's own ground, and
 * the outcome is carried by the plate behind the word rather than by the word
 * being tinted a different colour.
 */
export function ResultsHeader({ result, opponentName }: ResultsHeaderProps) {
  const isVictory = result === 'victory';
  const Icon = isVictory ? Trophy : Flag;

  return (
    <div className="arena-plate animate-turn-claim relative overflow-hidden rounded-2xl px-6 py-10 text-center">
      <div className="arena-floor pointer-events-none absolute inset-0" aria-hidden="true" />

      {/* A win is lit from behind the trophy, so the two outcomes are told
          apart before either word is read. */}
      {isVictory && (
        <div
          className="pointer-events-none absolute inset-0"
          aria-hidden="true"
          style={{
            background:
              'radial-gradient(55% 55% at 50% 32%, color-mix(in oklab, var(--color-brass) 38%, transparent) 0%, transparent 70%)',
          }}
        />
      )}

      <div className="relative">
        <span className={`mx-auto flex h-20 w-20 items-center justify-center rounded-full border-[3px] border-outline shadow-[0_5px_0_0_var(--color-outline)] ${
          isVictory ? 'bg-brass text-white' : 'bg-arena-glass text-arena-ink-muted'
        }`}>
          <Icon className="h-10 w-10" aria-hidden="true" strokeWidth={2} />
        </span>

        <h1 className={`display struck mt-4 text-6xl text-white ${isVictory ? '' : 'opacity-90'}`}>
          {isVictory ? 'Victory' : 'Defeat'}
        </h1>

        <p className="mt-2 break-words font-bold text-arena-ink">
          {isVictory ? `You beat ${opponentName}` : `${opponentName} beat you`}
        </p>
      </div>
    </div>
  );
}
