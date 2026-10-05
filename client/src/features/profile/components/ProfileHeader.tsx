import { Link } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';

/** The same rail the arena and the catalogue use, so every page has one exit. */
export function ProfileHeader() {
  return (
    <div className="panel flex items-center gap-2 rounded-lg bg-arena-deep px-2 py-1.5">
      <Link
        to="/menu"
        className="flex items-center gap-1.5 rounded-control px-2 py-1.5 text-sm font-semibold text-arena-ink-muted transition-colors hover:bg-arena-glass hover:text-arena-ink focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass"
      >
        <ArrowLeft className="h-4 w-4" aria-hidden="true" />
        <span className="hidden sm:inline">Menu</span>
      </Link>

      <div className="flex flex-1 justify-center">
        <span className="display text-lg text-arena-ink">Trainer profile</span>
      </div>

      <span className="w-16" aria-hidden="true" />
    </div>
  );
}
