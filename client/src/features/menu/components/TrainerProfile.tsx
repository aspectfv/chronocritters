import { Link } from 'react-router-dom';
import type { TrainerProfileProps } from '@features/menu/types';

export function TrainerProfile({ wins, losses }: TrainerProfileProps) {
  return (
    <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
      <div className="flex items-center gap-2 text-gray-700 mb-4">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <span className="font-semibold">Trainer Profile</span>
      </div>
      
      <p className="text-gray-600 mb-6">
        Manage your critter team and view your battle history.
      </p>
      
      <div className="flex gap-4 mb-6">
        <div className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium">
          Wins: {wins}
        </div>
        <div className="bg-gray-100 text-gray-800 px-3 py-1 rounded-full text-sm font-medium">
          Losses: {losses}
        </div>
      </div>
      
      <Link 
        to="/profile"
        className="w-full bg-white hover:bg-gray-50 text-gray-700 font-semibold py-3 px-4 rounded-lg border border-gray-300 transition-colors flex items-center justify-center gap-2"
      >
        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
          <path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" />
        </svg>
        View Profile
      </Link>
    </div>
  );
}