import { cn } from '@components/ui/cn';

/** The figure-over-label pattern, repeated across results and profile. */
export function Stat({ value, label, tone = 'ink', className }: {
  value: string | number;
  label: string;
  tone?: 'ink' | 'accent' | 'danger';
  className?: string;
}) {
  const valueTone = tone === 'accent' ? 'text-accent-ink' : tone === 'danger' ? 'text-danger-ink' : 'text-ink';
  return (
    <div className={cn('text-center', className)}>
      <p className={cn('text-3xl font-bold tabular-nums', valueTone)}>{value}</p>
      <p className="text-sm text-ink-muted">{label}</p>
    </div>
  );
}
