import { useEffect, useState } from 'react';
import { useLoaderData, useSearchParams } from 'react-router-dom';
import type { GetPlayerOverviewQuery } from '@/gql/graphql';
import { TEAM_SIZE, type MenuPlayer } from '@features/menu/types';
import { TrainerCard } from '@features/menu/components/TrainerCard';
import { FindMatch } from '@features/menu/components/FindMatch';
import { MenuLinks } from '@features/menu/components/MenuLinks';
import { LogoutButton } from '@features/menu/components/LogoutButton';

const notices: Record<string, string> = {
  'battle-ended': 'That battle is no longer running, so you have been returned to the menu.',
};

const teamStorageKey = (playerId: string) => `chronocritters.team.${playerId}`;

/**
 * The team this player last queued with, if it still fits their roster, and
 * otherwise the first critters they own. It is a convenience kept in this
 * browser, so a missing or unreadable one is not an error.
 */
function initialTeam(player: MenuPlayer | null | undefined): string[] {
  const owned = player?.roster?.flatMap((critter) => (critter?.id ? [critter.id] : [])) ?? [];
  const teamSize = Math.min(TEAM_SIZE, owned.length);

  try {
    const saved: unknown = JSON.parse(localStorage.getItem(teamStorageKey(player?.id ?? '')) ?? 'null');
    if (Array.isArray(saved) && saved.length === teamSize && saved.every((id) => owned.includes(id))) {
      return saved;
    }
  } catch {
    // Storage can be blocked or hold something unreadable; the default stands in.
  }

  return owned.slice(0, teamSize);
}

function MenuPage() {
  const loaderData = useLoaderData() as GetPlayerOverviewQuery;
  const [searchParams] = useSearchParams();
  const player = loaderData?.getPlayer;
  const notice = notices[searchParams.get('notice') ?? ''];

  const teamSize = Math.min(TEAM_SIZE, player?.roster?.filter(Boolean).length ?? 0);
  const [team, setTeam] = useState(() => initialTeam(player));

  useEffect(() => {
    if (!player?.id || team.length !== teamSize) return;
    try {
      localStorage.setItem(teamStorageKey(player.id), JSON.stringify(team));
    } catch {
      // Not remembering the team only costs the player re-picking it.
    }
  }, [player?.id, team, teamSize]);

  const toggleCritter = (critterId: string) => {
    setTeam((current) => {
      if (current.includes(critterId)) return current.filter((id) => id !== critterId);
      return current.length < teamSize ? [...current, critterId] : current;
    });
  };

  return (
    <main className="min-h-screen bg-arena text-arena-ink">
      <div className="mx-auto flex min-h-screen max-w-3xl flex-col justify-center gap-3 p-3 sm:p-5">
        {notice && (
          <div role="status" className="rounded-sm border-2 border-outline bg-warn px-4 py-2.5 text-center text-sm font-bold text-white">
            {notice}
          </div>
        )}

        {player && <TrainerCard player={player} team={team} teamSize={teamSize} onToggle={toggleCritter} />}

        <FindMatch team={team} isTeamReady={teamSize > 0 && team.length === teamSize} />
        <MenuLinks />

        <div className="pt-2">
          <LogoutButton />
        </div>
      </div>
    </main>
  );
}

export default MenuPage;
