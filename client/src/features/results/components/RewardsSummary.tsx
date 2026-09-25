import { Sparkles } from 'lucide-react';
import { Surface } from '@components/ui/Surface';
import { CountUp } from '@components/ui/CountUp';

export const RewardsSummary = ({ expGained }: { expGained: number }) => (
  <Surface tone="accent" className="flex h-full flex-col">
    <h2 className="mb-4 flex items-center gap-2 text-sm font-medium text-accent-ink">
      <Sparkles className="h-4 w-4" aria-hidden="true" />
      Rewards
    </h2>
    <div className="flex flex-1 flex-col items-center justify-center rounded-control bg-surface px-4 py-6 text-center">
      <p className="numeral text-5xl text-accent-ink">
        +<CountUp to={expGained} delayMs={300} />
      </p>
      <p className="mt-1 text-sm text-ink-muted">Experience earned</p>
    </div>
  </Surface>
);
