import { Clock, Swords, Repeat } from 'lucide-react';
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
    <div className="panel rounded-lg bg-arena-deep p-4">
      <h2 className="mb-3 text-sm font-bold text-arena-ink">How it went</h2>
      {/* Was three items across with no wrap or gutter, which collided below 400px. */}
      <dl className="grid grid-cols-1 gap-2.5 sm:grid-cols-3">
        {stats.map(({ key, icon: Icon, label }) => (
          <div key={key} className="plaque px-3 py-3.5 text-center">
            <Icon className="mx-auto mb-1 h-4 w-4 text-brass-ink" aria-hidden="true" />
            <dd className="numeral text-3xl text-arena-ink">{values[key]}</dd>
            <dt className="mt-0.5 text-xs font-bold text-arena-ink-muted">{label}</dt>
          </div>
        ))}
      </dl>
    </div>
  );
};
