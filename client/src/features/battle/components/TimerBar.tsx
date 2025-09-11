import type { TimerBarProps } from '@features/battle/types';

export function TimerBar({ timeRemaining }: TimerBarProps) {
  const percentage = Math.max(0, (timeRemaining / 30) * 100);

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 my-6">
      <div className="flex justify-between items-center text-md">
        <div className="flex items-center gap-2 font-semibold text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>Time Remaining</span>
        </div>
        <div className="w-full mx-4 bg-gray-200 rounded-full h-2.5">
          <div className="bg-green-600 h-2.5 rounded-full" style={{ width: `${percentage}%` }}></div>
        </div>
        <span className="font-bold text-lg text-gray-800">{timeRemaining}s</span>
      </div>
    </div>
  );
}