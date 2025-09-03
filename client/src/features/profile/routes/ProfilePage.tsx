import { Link } from 'react-router-dom';

function ProfilePage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900">ChronoCritters</h1>
            </div>
            <div className="flex items-center space-x-4">
              <Link to="/menu" className="text-gray-600 hover:text-gray-900">Menu</Link>
              <Link to="/" className="text-gray-600 hover:text-gray-900">Logout</Link>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Player Profile</h1>
          
          <div className="bg-white rounded-lg shadow p-6 space-y-6">
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Player Stats</h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="text-sm text-gray-600">Battles Won</p>
                  <p className="text-2xl font-bold text-gray-900">0</p>
                </div>
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="text-sm text-gray-600">Critters Collected</p>
                  <p className="text-2xl font-bold text-gray-900">0</p>
                </div>
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="text-sm text-gray-600">Experience</p>
                  <p className="text-2xl font-bold text-gray-900">0</p>
                </div>
              </div>
            </div>
            
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Critter Collection</h2>
              <p className="text-gray-600">No critters collected yet.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProfilePage;
