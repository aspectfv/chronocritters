import type { ReactNode } from 'react';
import { cn } from '@components/ui/cn';

const tones = {
  neutral: 'bg-surface-sunk text-ink-muted border-line',
  accent: 'bg-accent-soft text-accent-ink border-accent/25',
  danger: 'bg-danger-soft text-danger-ink border-danger/25',
  warn: 'bg-warn-soft text-warn-ink border-warn/30',
} as const;

/** Type badges, status effects and result pills, previously three patterns. */
export function Chip({ tone = 'neutral', className, children }: {
  tone?: keyof typeof tones;
  className?: string;
  children: ReactNode;
}) {
  return (
    <span className={cn('inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-xs font-semibold', tones[tone], className)}>
      {children}
    </span>
  );
}
