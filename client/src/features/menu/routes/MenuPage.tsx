import { useLoaderData, useSearchParams } from 'react-router-dom';
import type { GetPlayerOverviewQuery } from '@/gql/graphql';
import { TrainerCard } from '@features/menu/components/TrainerCard';
import { FindMatch } from '@features/menu/components/FindMatch';
import { MenuLinks } from '@features/menu/components/MenuLinks';
import { LogoutButton } from '@features/menu/components/LogoutButton';

const notices: Record<string, string> = {
  'battle-ended': 'That battle is no longer running, so you have been returned to the menu.',
};

function MenuPage() {
  const loaderData = useLoaderData() as GetPlayerOverviewQuery;
  const [searchParams] = useSearchParams();
  const player = loaderData?.getPlayer;
  const notice = notices[searchParams.get('notice') ?? ''];

  return (
    <main className="min-h-screen bg-arena text-arena-ink">
      <div className="mx-auto flex min-h-screen max-w-3xl flex-col justify-center gap-3 p-3 sm:p-5">
        {notice && (
          <div role="status" className="rounded-sm border-2 border-outline bg-warn px-4 py-2.5 text-center text-sm font-bold text-white">
            {notice}
          </div>
        )}

        {player && <TrainerCard player={player} />}

        <FindMatch />
        <MenuLinks />

        <div className="pt-2">
          <LogoutButton />
        </div>
      </div>
    </main>
  );
}

export default MenuPage;
