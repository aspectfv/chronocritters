import type { BattleAbility } from '@store/battle/types';
import type { MoveGridProps } from '@features/battle/types';
import { describeBattleEffect, getBattleEffectMeta, getCritterTypeFill, getCritterTypeIcon } from '@utils/utils';
import type { CritterType } from '@/gql/graphql';

function MoveButton({ ability, casterType, onClick, disabled }: {
  ability: BattleAbility;
  casterType: CritterType;
  onClick: () => void;
  disabled: boolean;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      title={ability.description}
      className={`key group relative rounded-md p-3 text-left text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 ${getCritterTypeFill(casterType)}`}
    >
      <p className="truncate text-[15px] font-black tracking-tight drop-shadow-[0_1px_0_rgba(0,0,0,0.35)]">
        {ability.name}
      </p>

      {/* Every effect, not just the first. A move that hits and then burns is
          picked for the burn, and reading only effects[0] hid it. */}
      <div className="mt-1.5 flex flex-wrap items-center gap-1.5">
        {ability.effects.map((effect) => (
          <span
            key={effect.id}
            className="numeral inline-flex items-center gap-1 rounded border-2 border-outline bg-outline/80 px-1.5 py-0.5 text-[11px] leading-tight text-white"
          >
            <span aria-hidden="true">{getBattleEffectMeta(effect).icon}</span>
            {describeBattleEffect(effect)}
          </span>
        ))}
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
