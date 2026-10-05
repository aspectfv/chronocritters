import { Sparkles } from 'lucide-react';
import { CountUp } from '@components/ui/CountUp';

export const RewardsSummary = ({ expGained }: { expGained: number }) => (
  <div className="panel flex h-full flex-col rounded-lg bg-arena-deep p-4">
    <h2 className="mb-3 flex items-center gap-2 text-sm font-bold text-arena-ink">
      <Sparkles className="h-4 w-4 text-brass-ink" aria-hidden="true" />
      Earned
    </h2>
    <div className="plaque flex flex-1 flex-col items-center justify-center px-4 py-6 text-center">
      <p className="numeral text-5xl text-brass-ink">
        +<CountUp to={expGained} delayMs={300} />
      </p>
      <p className="mt-1 text-xs font-bold text-arena-ink-muted">Experience</p>
    </div>
  </div>
);
