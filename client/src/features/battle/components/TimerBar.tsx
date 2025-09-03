interface TimerBarProps {
  timeRemaining: number;
}

export function TimerBar({ timeRemaining }: TimerBarProps) {
  // Assuming a total turn time of 30 seconds for calculating the percentage.
  // The `Math.max(0, ...)` ensures the percentage doesn't go below zero.
  const percentage = Math.max(0, (timeRemaining / 30) * 100);

  return (
    <div className="border border-green-200 bg-white rounded-lg p-4 my-6">
      <div className="flex justify-between items-center text-sm text-gray-600 mb-2">
        {/* Label for the timer */}
        <span>Time Remaining</span>
        
        {/* The numeric countdown */}
        <span>{timeRemaining}s</span>
      </div>
      
      {/* The background of the progress bar */}
      <div className="w-full bg-green-100 rounded-full h-2.5">
        {/* The foreground (filled) part of the bar */}
        <div 
          className="bg-gradient-to-r from-green-500 to-yellow-400 h-2.5 rounded-full transition-all duration-500 ease-linear" 
          style={{ width: `${percentage}%` }}
        ></div>
      </div>
    </div>
  );
}