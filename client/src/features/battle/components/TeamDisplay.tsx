import type { TeamCritter } from '@store/battle/types';

interface TeamDisplayProps {
  title: string;
  team: TeamCritter[];
}

export function TeamDisplay({ title, team }: TeamDisplayProps) {
  return (
    <div>
      <h3 className="font-semibold text-green-800 mb-2">{title}</h3>
      <div className="bg-green-50 border border-green-200 rounded-lg p-4 flex justify-center gap-4 h-full">
        {team.map(critter => (
          <div key={critter.name} className="text-center">
            <div className="w-12 h-12 bg-green-100 rounded-full mx-auto flex items-center justify-center text-xl">
              {/* This could be made dynamic based on critter.type */}
              <span>💧</span> 
            </div>
            <p className="text-xs mt-1 text-gray-700">{critter.name}</p>
            <p className="text-xs text-gray-500">{critter.currentHp}/{critter.maxHp}</p>
          </div>
        ))}
      </div>
    </div>
  );
}