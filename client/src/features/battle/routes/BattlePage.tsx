import { Link } from 'react-router-dom';

function BattlePage() {
  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <nav className="bg-gray-800 border-b border-gray-700">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-white">ChronoCritters - Battle</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Link to="/menu" className="text-gray-300 hover:text-white">Menu</Link>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-white mb-8">Battle Arena</h1>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
            <div className="bg-gray-800 p-6 rounded-lg">
              <h2 className="text-xl font-semibold mb-4">Your Critter</h2>
              <div className="bg-gray-700 h-32 rounded-lg flex items-center justify-center">
                <p className="text-gray-400">No critter selected</p>
              </div>
              <div className="mt-4">
                <div className="bg-gray-700 rounded-full h-4">
                  <div className="bg-green-500 h-4 rounded-full" style={{ width: '100%' }}></div>
                </div>
                <p className="text-sm text-gray-400 mt-1">HP: 100/100</p>
              </div>
            </div>
            
            <div className="bg-gray-800 p-6 rounded-lg">
              <h2 className="text-xl font-semibold mb-4">Opponent Critter</h2>
              <div className="bg-gray-700 h-32 rounded-lg flex items-center justify-center">
                <p className="text-gray-400">Waiting for opponent...</p>
              </div>
              <div className="mt-4">
                <div className="bg-gray-700 rounded-full h-4">
                  <div className="bg-red-500 h-4 rounded-full" style={{ width: '100%' }}></div>
                </div>
                <p className="text-sm text-gray-400 mt-1">HP: 100/100</p>
              </div>
            </div>
          </div>
          
          <div className="space-x-4">
            <button className="bg-red-500 hover:bg-red-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors">
              Attack
            </button>
            <button className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors">
              Defend
            </button>
            <Link
              to="/results"
              className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors inline-block"
            >
              End Battle
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BattlePage;
