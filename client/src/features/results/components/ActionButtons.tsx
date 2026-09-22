import { Link } from 'react-router-dom';

export const ActionButtons = () => {
  return (
    <div className="flex justify-center items-center mt-8">
      <Link
        to="/menu"
        className="bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-6 rounded-lg transition-colors"
      >
        Back to Menu
      </Link>
    </div>
  );
};
