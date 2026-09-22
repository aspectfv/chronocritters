import { useEffect, useRef } from 'react';
import type { BattleLogProps } from '@features/battle/types';
import { getBattleLogStyle } from '@utils/utils';

export function BattleLog({ log }: BattleLogProps) {
  const logContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (logContainerRef.current) {
      logContainerRef.current.scrollTop = logContainerRef.current.scrollHeight;
    }
  }, [log]);

  return (
    <div>
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h3 className="text-center font-bold text-lg text-gray-800 mb-4">Battle Log</h3>
        <div ref={logContainerRef} className="space-y-2 max-h-48 overflow-y-auto pr-2" aria-live="polite">
          {log.length > 0 ? (
            log.map((message, index) => {
              const isLatest = index === log.length - 1;

              return (
                <div
                  key={index}
                  className={`rounded-lg p-3 ${getBattleLogStyle(message)} ${isLatest ? 'animate-log-enter ring-2 ring-green-300' : ''}`}
                >
                  <p className="font-medium text-sm">{message}</p>
                </div>
              );
            })
          ) : (
            <div className="bg-gray-50 p-3 rounded-lg">
              <p className="text-gray-500 italic text-center font-medium">Waiting for battle to start...</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
