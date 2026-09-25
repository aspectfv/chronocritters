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
      className={`key group relative rounded-md p-3 text-left text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 ${getCritterTypeFill(casterType)}`}
    >
      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0">
          <p className="truncate text-[15px] font-black tracking-tight drop-shadow-[0_1px_0_rgba(0,0,0,0.35)]">
            {ability.name}
          </p>
          <p className="mt-0.5 line-clamp-2 text-xs font-medium text-white/85">
            {effect?.description ?? 'No effect description.'}
          </p>
        </div>

        <span className="flex shrink-0 flex-col items-center gap-1">
          {power !== null && (
            <span className="numeral rounded border-2 border-outline bg-outline/85 px-2 py-0.5 text-base leading-tight text-white">
              {power}
            </span>
          )}
          {meta && <span className="text-sm" aria-hidden="true">{meta.icon}</span>}
        </span>
      </div>
    </button>
  );
}

export function MoveGrid({ abilities, casterType, onAbilityClick, isPlayerTurn, isResolving }: MoveGridProps) {
  const heading = isResolving
    ? 'Resolving your move...'
    : isPlayerTurn ? 'Choose a move' : 'Waiting for your opponent';

  return (
    <div className="panel rounded-lg bg-arena-deep p-3">
      <div className="mb-2.5 flex items-center justify-between px-0.5">
        <span className="text-sm font-bold text-arena-ink">{heading}</span>
        <span className={`flex items-center gap-1 rounded-full border-2 border-outline px-2 py-0.5 text-[11px] font-black text-white ${getCritterTypeFill(casterType)}`}>
          <span aria-hidden="true">{getCritterTypeIcon(casterType)}</span>
          {casterType}
        </span>
      </div>
      <div className="grid grid-cols-1 gap-2.5 sm:grid-cols-2">
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
