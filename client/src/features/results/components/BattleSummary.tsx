import { Clock, Swords, Repeat } from 'lucide-react';
import { Surface } from '@components/ui/Surface';
import { CountUp } from '@components/ui/CountUp';
import { formatDuration } from '@utils/utils';

const stats = [
  { key: 'duration', icon: Clock, label: 'Duration' },
  { key: 'turns', icon: Repeat, label: 'Turns played' },
  { key: 'damage', icon: Swords, label: 'Damage dealt' },
] as const;

export const BattleSummary = ({ turnCount, playerDamageDealt, duration }: {
  turnCount: number; playerDamageDealt: number; duration: number;
}) => {
  const values = {
    duration: <CountUp to={duration} delayMs={500} format={formatDuration} />,
    turns: <CountUp to={turnCount} delayMs={620} />,
    damage: <CountUp to={playerDamageDealt} delayMs={740} />,
  };

  return (
    <Surface>
      <h2 className="mb-4 text-center text-[11px] font-bold uppercase tracking-widest text-ink-muted">
        Battle summary
      </h2>
      {/* Was three items across with no wrap or gutter, which collided below 400px. */}
      <dl className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        {stats.map(({ key, icon: Icon, label }) => (
          <div key={key} className="rounded-control bg-surface-sunk px-3 py-4 text-center">
            <Icon className="mx-auto mb-1 h-4 w-4 text-ink-faint" aria-hidden="true" />
            <dd className="text-3xl font-bold tabular-nums text-ink">{values[key]}</dd>
            <dt className="mt-0.5 text-sm text-ink-muted">{label}</dt>
          </div>
        ))}
      </dl>
    </Surface>
  );
};
