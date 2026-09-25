import { useLobbyStore } from '@store/lobby/useLobbyStore';
import type { TrainerCardProps } from '@features/menu/types';
import { PartyRail } from '@features/menu/components/PartyRail';
import { getConnectionStatusStyle } from '@utils/utils';

/**
 * The trainer card, standing on the arena's own ground.
 *
 * The menu was three identical cards on a flat field, which said nothing about
 * whose save this is. Everything here is specific to the player: their record
 * struck as figures, how close that is to even, and the party they are carrying.
 */
export function TrainerCard({ player }: TrainerCardProps) {
  const connectionStatus = useLobbyStore((state) => state.connectionStatus);
  const connection = getConnectionStatusStyle(connectionStatus);

  const wins = player.stats?.wins ?? 0;
  const losses = player.stats?.losses ?? 0;
  const battles = wins + losses;
  const winRate = battles > 0 ? Math.round((wins / battles) * 100) : 0;

  return (
    <section className="arena-plate relative overflow-hidden rounded-2xl p-4 sm:p-6">
      {/* The ground, without the horizon rule the arena draws: here it lands
          across the party labels rather than behind the critters. */}
      <div className="arena-floor pointer-events-none absolute inset-0" aria-hidden="true" />

      <div className="relative flex flex-col gap-5">
        <div className="flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="display text-3xl leading-none text-arena-ink sm:text-4xl">Chrono Critters</p>
            <p className="mt-1.5 flex items-center gap-2 text-sm text-arena-ink-muted">
              <span className="font-bold text-arena-ink">{player.username}</span>
              <span className="flex items-center gap-1.5" title={connection.text}>
                <span className={`h-2 w-2 rounded-full ${connection.color}`} aria-hidden="true" />
                {connection.text}
              </span>
            </p>
          </div>

          <div className="plaque px-4 py-2">
            <div className="flex items-baseline gap-3">
              <span className="numeral text-2xl text-vital">{wins}</span>
              <span className="text-sm font-bold text-arena-ink-muted">won</span>
              <span className="numeral text-2xl text-ruby">{losses}</span>
              <span className="text-sm font-bold text-arena-ink-muted">lost</span>
            </div>
            <div className="mt-1.5 flex items-center gap-2">
              <span className="well block h-2.5 w-28 overflow-hidden rounded-sm">
                <span className="hp-fill block h-full bg-vital" style={{ width: `${winRate}%` }} />
              </span>
              <span className="numeral text-[11px] text-arena-ink">{winRate}%</span>
            </div>
          </div>
        </div>

        <div>
          <p className="mb-2 text-xs font-bold text-arena-ink">Your party</p>
          <PartyRail roster={player.roster} />
        </div>
      </div>
    </section>
  );
}
