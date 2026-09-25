import { Link } from 'react-router-dom';
import { Home, Swords } from 'lucide-react';

/** The component was plural and held one control, with the menu as the only exit. */
export const ActionButtons = () => (
  <div className="flex flex-col gap-2.5 sm:flex-row">
    <Link
      to="/menu?queue=1"
      className="key flex flex-1 items-center justify-center gap-2.5 rounded-lg bg-brass px-4 py-4 text-lg font-black text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
    >
      <Swords className="h-5 w-5" aria-hidden="true" />
      Battle again
    </Link>
    <Link
      to="/menu"
      className="key flex flex-1 items-center justify-center gap-2.5 rounded-lg bg-arena-deep px-4 py-4 text-lg font-black text-arena-ink focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
    >
      <Home className="h-5 w-5" aria-hidden="true" />
      Back to the menu
    </Link>
  </div>
);
