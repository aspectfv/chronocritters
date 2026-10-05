import { Link } from 'react-router-dom';
import { BookOpen, User } from 'lucide-react';

const links = [
  { to: '/profile', icon: User, label: 'Trainer profile', blurb: 'Your team and your battle history' },
  { to: '/catalog', icon: BookOpen, label: 'Critter catalogue', blurb: 'Every critter, and what beats it' },
];

/** The two places worth going that are not a battle. */
export function MenuLinks() {
  return (
    <div className="grid grid-cols-1 gap-2.5 sm:grid-cols-2">
      {links.map(({ to, icon: Icon, label, blurb }) => (
        <Link
          key={to}
          to={to}
          className="key flex items-center gap-3 rounded-lg bg-arena-deep px-4 py-3 text-left focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
        >
          <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md border-2 border-outline bg-arena-glass text-brass-ink">
            <Icon className="h-4 w-4" aria-hidden="true" />
          </span>
          <span className="min-w-0">
            <span className="block truncate text-sm font-black text-arena-ink">{label}</span>
            <span className="block truncate text-xs text-arena-ink-muted">{blurb}</span>
          </span>
        </Link>
      ))}
    </div>
  );
}
