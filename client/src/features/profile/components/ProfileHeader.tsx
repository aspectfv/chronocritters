import { Link } from 'react-router-dom';

export function ProfileHeader() {
  return (
    <div className="mb-6">
      <Link
        to="/menu"
        className="inline-block bg-white text-gray-700 font-semibold py-2 px-4 rounded-lg shadow-sm border border-gray-200 hover:bg-gray-50 transition-colors"
      >
        &larr; Back to Menu
      </Link>
      <h1 className="text-4xl font-bold text-center text-green-800 mt-4">
        Trainer Profile
      </h1>
    </div>
  );
}