import { Link } from 'react-router-dom';

function MenuPage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">ChronoCritters</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Link to="/profile" className="text-gray-600 hover:text-gray-900">Profile</Link>
              <Link to="/" className="text-gray-600 hover:text-gray-900">Logout</Link>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Main Menu</h1>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-2xl mx-auto">
            <Link
              to="/battle"
              className="bg-blue-500 hover:bg-blue-600 text-white p-8 rounded-lg font-semibold text-xl transition-colors"
            >
              Start Battle
            </Link>
            <Link
              to="/profile"
              className="bg-green-500 hover:bg-green-600 text-white p-8 rounded-lg font-semibold text-xl transition-colors"
            >
              View Profile
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MenuPage;
