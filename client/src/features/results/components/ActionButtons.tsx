import { Link } from 'react-router-dom';
import { Home, Swords } from 'lucide-react';
import { Button } from '@components/ui/Button';

/** The component was plural and held one control, with the menu as the only exit. */
export const ActionButtons = () => (
  <div className="flex flex-col gap-3 sm:flex-row sm:justify-center">
    <Button as={Link} to="/menu?queue=1" className="sm:min-w-52">
      <Swords className="h-5 w-5" aria-hidden="true" />
      Battle again
    </Button>
    <Button as={Link} to="/menu" variant="secondary" className="sm:min-w-52">
      <Home className="h-5 w-5" aria-hidden="true" />
      Back to menu
    </Button>
  </div>
);
