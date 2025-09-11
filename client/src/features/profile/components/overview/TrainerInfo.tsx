import type { TrainerInfoProps } from "@features/profile/types";

// mock data for fields unimplemented in backend
const mockTrainerData = {
  title: "Gold Trainer",
  level: 15,
  experience: 2450,
};

export function TrainerInfo({ username }: TrainerInfoProps) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 h-full">
      <div className="flex items-center gap-3 mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-green-700" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <h3 className="font-semibold text-lg text-gray-800">Trainer Info</h3>
      </div>
      <div className="text-center">
        <div className="w-24 h-24 bg-green-100 rounded-full mx-auto flex items-center justify-center mb-2">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-green-500" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
          </svg>
        </div>
        <p className="text-2xl font-bold text-gray-800">{username ? username : 'Error!'}</p>
        <span className="text-sm font-semibold text-white bg-green-600 px-3 py-1 rounded-full">{mockTrainerData.title}</span>
      </div>
      <div className="mt-8 space-y-4">
        <div className="flex justify-between items-center text-md">
          <span className="text-gray-500">Level</span>
          <span className="font-bold text-gray-800">{mockTrainerData.level}</span>
        </div>
        <div className="flex justify-between items-center text-md">
          <span className="text-gray-500">Experience</span>
          <span className="font-bold text-gray-800">{mockTrainerData.experience} XP</span>
        </div>
      </div>
    </div>
  );
}