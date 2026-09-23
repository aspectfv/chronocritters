import type { BattleAbility } from '@store/battle/types';
import type { MoveGridProps } from '@features/battle/types';
import { getAbilityPower, getBattleEffectMeta, getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';
import type { CritterType } from '@/gql/graphql';

function MoveButton({ ability, casterType, onClick, disabled }: {
  ability: BattleAbility;
  casterType: CritterType;
  onClick: () => void;
  disabled: boolean;
}) {
  const effect = ability.effects[0];
  const power = getAbilityPower(effect);
  const meta = effect ? getBattleEffectMeta(effect) : null;

  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className="group relative overflow-hidden rounded-lg border border-brass/35 bg-arena-deep p-3 text-left transition-all hover:border-brass hover:bg-arena-glass/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass focus-visible:ring-offset-2 focus-visible:ring-offset-arena disabled:opacity-40 disabled:hover:border-brass/35"
    >
      {/* Type stripe: the move grid's job is to be readable at a glance. */}
      <span className={`absolute inset-y-0 left-0 w-1 ${getCritterTypeFill(casterType)}`} aria-hidden="true" />

      <div className="flex items-start justify-between gap-2 pl-2">
        <div className="min-w-0">
          <p className="truncate font-bold text-arena-ink">{ability.name}</p>
          <p className="mt-0.5 line-clamp-2 text-xs text-arena-ink-muted">
            {effect?.description ?? 'No effect description.'}
          </p>
        </div>
        <div className="flex shrink-0 flex-col items-end gap-1">
          {power !== null && (
            <span className="rounded bg-arena-glass px-1.5 py-0.5 text-sm font-black tabular-nums text-brass-ink">{power}</span>
          )}
          {meta && <span className="text-sm" aria-hidden="true">{meta.icon}</span>}
        </div>
      </div>
    </button>
  );
}

export function MoveGrid({ abilities, casterType, onAbilityClick, isPlayerTurn, isResolving }: MoveGridProps) {
  const heading = isResolving
    ? 'Resolving your move...'
    : isPlayerTurn ? 'Choose a move' : 'Waiting for your opponent';

  return (
    <div className="rounded-xl border border-brass/30 bg-arena-deep p-3 shadow-card">
      <div className="mb-2 flex items-center justify-between px-1">
        <span className="text-[11px] font-bold uppercase tracking-widest text-brass-dim">{heading}</span>
        <span className="flex items-center gap-1 text-[11px] text-arena-ink-muted">
          <span aria-hidden="true">{getCritterTypeIcon(casterType)}</span>
          {casterType}
        </span>
      </div>
      <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
        {abilities.map((ability) => (
          <MoveButton
            key={ability.id}
            ability={ability}
            casterType={casterType}
            onClick={() => onAbilityClick(ability.id)}
            disabled={!isPlayerTurn}
          />
        ))}
      </div>
    </div>
  );
}
