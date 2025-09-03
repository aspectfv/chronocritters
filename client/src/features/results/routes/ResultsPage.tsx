import { Link } from 'react-router-dom';

function ResultsPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-900 to-purple-900 text-white">
      <nav className="bg-black bg-opacity-30 border-b border-white border-opacity-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-white">ChronoCritters - Results</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Link to="/menu" className="text-gray-300 hover:text-white">Menu</Link>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-6xl font-bold text-white mb-4">Victory!</h1>
          <p className="text-xl text-blue-200 mb-8">You have won the battle!</p>
          
          <div className="bg-black bg-opacity-30 rounded-lg p-8 max-w-2xl mx-auto mb-8">
            <h2 className="text-2xl font-semibold mb-6">Battle Summary</h2>
            
            <div className="space-y-4">
              <div className="flex justify-between">
                <span className="text-gray-300">Battle Duration:</span>
                <span className="text-white font-semibold">2:45</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-300">Experience Gained:</span>
                <span className="text-green-400 font-semibold">+150 XP</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-300">Coins Earned:</span>
                <span className="text-yellow-400 font-semibold">+50 Coins</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-300">New Critters:</span>
                <span className="text-purple-400 font-semibold">1 Found</span>
              </div>
            </div>
          </div>
          
          <div className="space-x-4">
            <Link
              to="/battle"
              className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold transition-colors inline-block"
            >
              Battle Again
            </Link>
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
