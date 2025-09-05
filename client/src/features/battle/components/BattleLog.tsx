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
    <div className="my-6">
      <h3 className="text-center font-bold text-green-800 mb-4">Battle Log</h3>
      <div
        ref={logContainerRef}
        className="bg-green-50 border border-green-200 rounded-lg p-4 h-40 overflow-y-auto space-y-2 text-left"
      >
        {log.length > 0 ? (
          log.map((message, index) => (
            <p key={index} className="text-gray-700 text-sm">
              <span className="font-semibold text-green-700">&gt;</span> {message}
            </p>
          ))
        ) : (
          <p className="text-gray-500 italic text-center">Waiting for action...</p>
        )}
      </div>
    </div>
  );
}