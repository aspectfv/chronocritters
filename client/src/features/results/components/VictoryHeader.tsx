import type { VictoryHeaderProps } from "@features/results/types";

export const VictoryHeader = ({ opponentName }: VictoryHeaderProps) => {
  return (
    <div className="bg-green-50 border border-green-200 rounded-lg p-8 text-center">
      <div className="flex justify-center items-center mb-4">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14zM4 21h4a2 2 0 002-2v-7a2 2 0 00-2-2H4a2 2 0 00-2 2v7a2 2 0 002 2z" />
        </svg>
      </div>
      <h1 className="text-4xl font-bold text-green-800">Victory!</h1>
      <p className="text-gray-600 mt-2">You defeated {opponentName}</p>
      <div className="inline-block bg-green-600 text-white font-bold px-6 py-2 rounded-full mt-4 text-sm">
        Winner
      </div>
    </div>
  );
};