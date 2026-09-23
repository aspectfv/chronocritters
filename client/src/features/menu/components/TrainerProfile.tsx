import { Link } from 'react-router-dom';
import { User } from 'lucide-react';
import { Surface } from '@components/ui/Surface';
import { Button } from '@components/ui/Button';
import { Chip } from '@components/ui/Chip';
import type { TrainerProfileProps } from '@features/menu/types';

export function TrainerProfile({ wins, losses }: TrainerProfileProps) {
  return (
    <Surface className="flex h-full flex-col">
      <div className="mb-4 flex items-center gap-2 text-ink">
        <User className="h-5 w-5" aria-hidden="true" />
        <span className="font-semibold">Trainer Profile</span>
      </div>

      <p className="mb-6 text-ink-muted">
        Manage your critter team and view your battle history.
      </p>

      <div className="mb-6 flex flex-wrap gap-2">
        <Chip tone="accent">Wins: {wins}</Chip>
        <Chip tone={losses > 0 ? 'danger' : 'neutral'}>Losses: {losses}</Chip>
      </div>

      <Button as={Link} to="/profile" variant="secondary" block className="mt-auto">
        <User className="h-5 w-5" aria-hidden="true" />
        View Profile
      </Button>
    </Surface>
  );
}
