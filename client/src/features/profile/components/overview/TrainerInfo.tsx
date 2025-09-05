import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { GetTrainerInfoData, GetTrainerInfoVars } from '@features/profile/types';

const GET_TRAINER_INFO = gql`
  query GetTrainerInfo($id: ID!) {
    getPlayer(id: $id) {
      id
      username
    }
  }
`;

// mock data for fields unimplemented in backend
const mockTrainerData = {
  title: "Gold Trainer",
  level: 15,
  experience: 2450,
};

export function TrainerInfo() {
  const user = useAuthStore((state) => state.user);

  const { data, loading, error } = useQuery<GetTrainerInfoData, GetTrainerInfoVars>(GET_TRAINER_INFO, {
    variables: { 
      id: user!.id 
    },
    skip: !user,
  });

  const trainerName = loading ? 'Loading...' : error ? 'Error!' : data?.getPlayer?.username;

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 h-full">
      <h3 className="font-semibold text-green-800 mb-4 flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        Trainer Info
      </h3>
      <div className="text-center">
        <div className="w-24 h-24 bg-green-100 rounded-full mx-auto flex items-center justify-center mb-2">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-green-500" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        </div>
        <p className="text-2xl font-bold text-gray-800">{trainerName}</p>
        <span className="text-sm font-semibold text-yellow-600 bg-yellow-100 px-3 py-1 rounded-full">{mockTrainerData.title}</span>
      </div>
      <div className="mt-6 space-y-3">
        <div className="flex justify-between items-center text-sm">
          <span className="text-gray-500">Level</span>
          <span className="font-bold text-gray-700">{mockTrainerData.level}</span>
        </div>
        <div className="flex justify-between items-center text-sm">
          <span className="text-gray-500">Experience</span>
          <span className="font-bold text-gray-700">{mockTrainerData.experience} XP</span>
        </div>
      </div>
    </div>
  );
}