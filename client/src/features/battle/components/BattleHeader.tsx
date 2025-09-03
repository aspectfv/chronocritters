import { Link } from 'react-router-dom';

interface BattleHeaderProps {
  isPlayerTurn: boolean;
}

export function BattleHeader({ isPlayerTurn }: BattleHeaderProps) {
  return (
    <div className="text-center mb-4">
      <div className="flex justify-between items-center mb-4">
        <Link to="/menu" className="text-sm text-gray-600 hover:text-green-600 flex items-center gap-1">
          &larr; Back to Menu
        </Link>
        <div className="flex items-center gap-2 text-sm text-gray-500">
          <span className="w-2 h-2 bg-green-500 rounded-full"></span>
          Connected
        </div>
      </div>
      <h1 className="text-4xl font-bold text-green-700">Battle Arena</h1>
      {isPlayerTurn && (
        <div className="mt-2 inline-block bg-green-600 text-white text-sm font-bold px-4 py-1 rounded-full">
          Your Turn
        </div>
      )}
    </div>
  );
}