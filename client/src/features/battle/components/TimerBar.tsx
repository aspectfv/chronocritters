interface TimerBarProps {
  timeRemaining: number;
}

export function TimerBar({ timeRemaining }: TimerBarProps) {
  const percentage = Math.max(0, (timeRemaining / 30) * 100);

  return (
    <div className="border border-green-200 bg-white rounded-lg p-4 my-6">
      <div className="flex justify-between items-center text-sm text-gray-600 mb-2">
        <span>Time Remaining</span>
        <span>{timeRemaining}s</span>
      </div>
      <div className="w-full bg-green-100 rounded-full h-2.5">
        <div 
          className="bg-gradient-to-r from-green-500 to-yellow-400 h-2.5 rounded-full transition-all duration-500 ease-linear" 
          style={{ width: `${percentage}%` }}
        ></div>
      </div>
    </div>
  );
}