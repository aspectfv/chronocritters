import type { ResultsHeaderProps } from "@features/results/types";

export const ResultsHeader = ({ result, opponentName }: ResultsHeaderProps) => {
  const isVictory = result === 'victory';

  const headerConfig = {
    victory: {
      title: 'Victory!',
      message: `You defeated ${opponentName}`,
      styles: 'bg-green-50 border-green-200 text-green-800',
      iconColor: 'text-green-600',
      badge: 'Winner',
      badgeStyles: 'bg-green-600 text-white',
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3H14zM4 21h4a2 2 0 002-2v-7a2 2 0 00-2-2H4a2 2 0 00-2 2v7a2 2 0 002 2z" />
        </svg>
      )
    },
    defeat: {
      title: 'Defeat!',
      message: `You were defeated by ${opponentName}`,
      styles: 'bg-gray-50 border-gray-200 text-gray-800',
      iconColor: 'text-gray-500',
      badge: 'Defeated',
      badgeStyles: 'bg-gray-500 text-white',
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14.25l-4.25-4.25M5.75 14.25L10 10" />
        </svg>
      )
    }
  };

  const config = isVictory ? headerConfig.victory : headerConfig.defeat;

  return (
    <div className={`p-8 text-center rounded-lg ${config.styles}`}>
      <div className={`flex justify-center items-center mb-4 ${config.iconColor}`}>
        {config.icon}
      </div>
      <h1 className="text-4xl font-bold">{config.title}</h1>
      <p className="text-gray-600 mt-2">{config.message}</p>
      <div className={`inline-block font-bold px-6 py-2 rounded-full mt-4 text-sm ${config.badgeStyles}`}>
        {config.badge}
      </div>
    </div>
  );
};