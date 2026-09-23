import { useEffect, useRef } from 'react';
import type { BattleLogProps } from '@features/battle/types';
import { classifyBattleLog } from '@utils/utils';

const tones: Record<string, string> = {
  damage: 'text-danger',
  faint: 'text-brass-ink',
  switch: 'text-type-grass',
  status: 'text-type-toxic',
  system: 'text-arena-ink-muted',
};

/**
 * The log in its genre position: a text box under the battlefield, newest line
 * carried large, history scrollable behind it.
 */
export function BattleTextBox({ log }: BattleLogProps) {
  const scroller = useRef<HTMLDivElement>(null);
  const latest = log.at(-1);

  useEffect(() => {
    if (scroller.current) scroller.current.scrollTop = scroller.current.scrollHeight;
  }, [log]);

  return (
    <div className="rounded-xl border-2 border-brass/40 bg-arena-deep p-3 shadow-card">
      <p
        key={log.length}
        className="animate-log-enter min-h-[2.5rem] text-[15px] font-semibold leading-snug text-arena-ink"
        aria-live="polite"
      >
        {latest ?? 'Waiting for the battle to start...'}
      </p>

      {log.length > 1 && (
        <div ref={scroller} className="mt-2 max-h-20 space-y-1 overflow-y-auto border-t border-brass/20 pt-2 pr-1">
          {log.slice(0, -1).map((message, index) => (
            <p key={index} className={`text-xs ${tones[classifyBattleLog(message)]}`}>{message}</p>
          ))}
        </div>
      )}
    </div>
  );
}
