import type { TimerBarProps } from '@features/battle/types';

export function TimerBar({ timeRemaining, turnDuration }: TimerBarProps) {
  const percentage = Math.max(0, Math.min(100, (timeRemaining / turnDuration) * 100));
  const isCritical = timeRemaining <= 5;
  const isLow = timeRemaining <= 10;

  const fillTone = isCritical ? 'bg-red-500' : isLow ? 'bg-amber-400' : 'bg-green-600';
  const countTone = isCritical ? 'text-red-600' : isLow ? 'text-amber-600' : 'text-gray-800';

  return (
    <div className={`bg-white rounded-xl shadow-sm border p-4 my-6 transition-colors ${isCritical ? 'border-red-300' : 'border-gray-200'}`}>
      <div className="flex justify-between items-center text-md">
        <div className="flex items-center gap-2 font-semibold text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>Time Remaining</span>
        </div>
        <div className="w-full mx-4 bg-gray-200 rounded-full h-2.5">
          <div
            className={`h-2.5 rounded-full transition-[width,background-color] duration-1000 ease-linear ${fillTone}`}
            style={{ width: `${percentage}%` }}
          ></div>
        </div>
        <span className={`font-bold text-lg tabular-nums transition-colors ${countTone} ${isCritical ? 'animate-pulse' : ''}`}>
          {timeRemaining}s
        </span>
      </div>
    </div>
  );
}
