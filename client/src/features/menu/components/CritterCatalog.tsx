import { Link } from 'react-router-dom';
import { BookOpen } from 'lucide-react';
import { Surface } from '@components/ui/Surface';
import { Button } from '@components/ui/Button';

export function CritterCatalog() {
  return (
    <Surface className="flex h-full flex-col">
      <div className="mb-4 flex items-center gap-2 text-ink">
        <BookOpen className="h-5 w-5" aria-hidden="true" />
        <span className="font-semibold">Critter Catalogue</span>
      </div>

      <p className="mb-6 text-ink-muted">
        Every critter in the game, what it is made of, and what beats it.
      </p>

      <Button as={Link} to="/catalog" variant="secondary" block className="mt-auto">
        <BookOpen className="h-5 w-5" aria-hidden="true" />
        Open Catalogue
      </Button>
    </Surface>
  );
}
