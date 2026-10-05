import { User } from 'lucide-react';
import type { TrainerInfoProps } from '@features/profile/types';

export function TrainerInfo({ username, experience, level }: TrainerInfoProps) {
  return (
    <div className="panel flex h-full flex-col rounded-lg bg-arena-deep p-4">
      <h2 className="mb-3 text-sm font-bold text-arena-ink">Trainer</h2>

      <div className="flex items-center gap-3">
        <span className="flex h-16 w-16 shrink-0 items-center justify-center rounded-full border-[3px] border-outline bg-brass text-white shadow-[0_4px_0_0_var(--color-outline)]">
          <User className="h-8 w-8" aria-hidden="true" />
        </span>
        <p className="display min-w-0 truncate text-2xl text-arena-ink">{username || 'Unknown trainer'}</p>
      </div>

      <dl className="mt-4 grid grid-cols-2 gap-2.5">
        <div className="plaque px-3 py-2.5 text-center">
          <dd className="numeral text-2xl text-arena-ink">{level}</dd>
          <dt className="text-xs font-bold text-arena-ink-muted">Level</dt>
        </div>
        <div className="plaque px-3 py-2.5 text-center">
          <dd className="numeral text-2xl text-brass-ink">{experience}</dd>
          <dt className="text-xs font-bold text-arena-ink-muted">Experience</dt>
        </div>
      </dl>
    </div>
  );
}
