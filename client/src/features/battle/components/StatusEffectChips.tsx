import type { StatusEffectChipsProps } from '@features/battle/types';
import { getBattleEffectDuration, getBattleEffectMeta } from '@utils/utils';

export function StatusEffectChips({ effects, compact = false }: StatusEffectChipsProps) {
  if (effects.length === 0) {
    return null;
  }

  return (
    <div className={`flex flex-wrap justify-center ${compact ? 'gap-0.5' : 'gap-1.5'}`}>
      {effects.map((effect) => {
        const { label, icon, style } = getBattleEffectMeta(effect);
        const duration = getBattleEffectDuration(effect);

        if (compact) {
          return (
            <span key={effect.id} title={`${label}${duration ? ` (${duration})` : ''}`} className="text-xs leading-none">
              {icon}
            </span>
          );
        }

        return (
          <span
            key={effect.id}
            title={effect.description}
            className={`inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-xs font-semibold ${style}`}
          >
            <span aria-hidden="true">{icon}</span>
            {label}
            {duration !== null && <span className="opacity-70">{duration}</span>}
          </span>
        );
      })}
    </div>
  );
}
