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
    <div className="panel relative rounded-lg bg-arena-deep px-4 pb-3 pt-3.5">
      <p
        key={log.length}
        className="animate-log-enter min-h-[2.5rem] pr-6 text-[17px] font-bold leading-snug text-arena-ink"
        aria-live="polite"
      >
        {latest ?? 'Waiting for the battle to start...'}
      </p>

      {/* The advance marker the genre puts at the corner of its text box. It
          says the line is settled and the box is waiting. */}
      <span
        className="animate-advance absolute bottom-3 right-3 h-0 w-0 border-l-[7px] border-r-[7px] border-t-[9px] border-l-transparent border-r-transparent border-t-outline"
        aria-hidden="true"
      />

      {log.length > 1 && (
        <div ref={scroller} className="well mt-2.5 max-h-20 space-y-1 overflow-y-auto rounded-sm bg-arena px-2 py-1.5">
          {log.slice(0, -1).map((message, index) => (
            <p key={index} className={`text-xs ${tones[classifyBattleLog(message)]}`}>{message}</p>
          ))}
        </div>
      )}
    </div>
  );
}
