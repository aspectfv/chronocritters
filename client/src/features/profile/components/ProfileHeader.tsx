import { Link } from 'react-router-dom';

export function ProfileHeader() {
  return (
    <div className="mb-6">
      <Link
        to="/menu"
        className="inline-block bg-surface text-ink font-semibold py-2 px-4 rounded-lg shadow-sm border border-line hover:bg-surface-sunk transition-colors"
      >
        &larr; Back to Menu
      </Link>
      <h1 className="text-4xl font-bold text-center text-accent-ink mt-4">
        Trainer Profile
      </h1>
    </div>
  );
}