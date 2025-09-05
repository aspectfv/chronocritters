import { Link } from 'react-router-dom';

export const ActionButtons = () => {
  return (
    <div className="flex justify-center items-center gap-4 mt-8">
      <Link
        to="/menu"
        className="bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-6 rounded-lg transition-colors flex items-center gap-2"
      >
        Play Again
      </Link>
      <Link
        to="/menu"
        className="bg-white hover:bg-gray-100 text-gray-700 font-semibold py-3 px-6 rounded-lg border border-gray-200 transition-colors"
      >
        Back to Menu
      </Link>
    </div>
  );
};