import { useEffect } from 'react';
import { Link, useParams, useLocation } from 'react-router-dom'; // Import useLocation
import { useBattleStore } from '@store/battle/useBattleStore';

function ResultsPage() {
  const { battleId } = useParams();
  const { state } = useLocation();
  const { resetBattleState } = useBattleStore();

  const battleResult = state?.result; 

  useEffect(() => {
    return () => {
      resetBattleState();
    };
  }, [resetBattleState]);

  const isVictory = battleResult === 'victory';

  if (!battleResult) {
    return (
      <div className="min-h-screen bg-gray-800 text-white flex flex-col items-center justify-center">
        <h1 className="text-4xl font-bold mb-4">No Battle Results Found</h1>
        <p className="text-gray-400 mb-8">Return to the menu to start a new battle.</p>
        <Link
          to="/menu"
          className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors inline-block"
        >
          Return to Menu
        </Link>
      </div>
    );
  }

  return (
    <div className={`min-h-screen text-white transition-colors duration-500 ${isVictory ? 'bg-gradient-to-br from-blue-900 to-purple-900' : 'bg-gradient-to-br from-gray-800 to-black'}`}>
      <nav className="bg-black bg-opacity-30 border-b border-white border-opacity-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-white">Battle Results: {battleId?.substring(0, 8)}...</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Link to="/menu" className="text-gray-300 hover:text-white">Menu</Link>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-6xl font-bold text-white mb-4">{isVictory ? 'Victory!' : 'Defeat!'}</h1>
          <p className={`text-xl mb-8 ${isVictory ? 'text-blue-200' : 'text-gray-400'}`}>
            {isVictory ? 'You have won the battle!' : 'You have been defeated.'}
          </p>
          
          <div className="bg-black bg-opacity-30 rounded-lg p-8 max-w-2xl mx-auto mb-8">
            <h2 className="text-2xl font-semibold mb-6">Battle Summary</h2>
            
            <div className="space-y-4">
              <div className="flex justify-between">
                <span className="text-gray-300">Experience Gained:</span>
                <span className={`${isVictory ? 'text-green-400' : 'text-red-400'} font-semibold`}>
                  {isVictory ? '+150 XP' : '+25 XP'}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-300">Coins Earned:</span>
                <span className="text-yellow-400 font-semibold">
                  {isVictory ? '+50 Coins' : '+10 Coins'}
                </span>
              </div>
            </div>
          </div>
          
          <div className="space-x-4">
            <Link
              to="/menu"
              className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors inline-block"
            >
              Return to Menu
            </Link>
            <Link
              to="/profile"
              className="bg-purple-500 hover:bg-purple-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors inline-block"
            >
              View Profile
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ResultsPage;