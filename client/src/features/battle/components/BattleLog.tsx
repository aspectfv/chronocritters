interface BattleLogProps {
  log: string[];
}

export function BattleLog({ log }: BattleLogProps) {
  // Display only the latest message from the log array
  const latestMessage = log.length > 0 ? log[log.length - 1] : 'Waiting for action...';

  return (
    <div className="my-6">
      <h3 className="text-center font-bold text-green-800 mb-4">Battle Log</h3>
      <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
        <p className="text-gray-600 italic">{latestMessage}</p>
      </div>
    </div>
  );
}