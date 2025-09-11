import { useEffect, useRef } from 'react';
import type { BattleLogProps } from '@features/battle/types';

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
        <div ref={logContainerRef} className="space-y-3 max-h-48 overflow-y-auto pr-2">
          {log.length > 0 ? (
            log.map((message, index) => (
              <div key={index} className="bg-cyan-50/60 p-3 rounded-lg">
                <p className="text-gray-800 font-medium text-sm">{message}</p>
              </div>
            ))
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