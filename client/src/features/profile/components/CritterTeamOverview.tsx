import { CritterCard } from './CritterCard';

const mockTeam = [
  { name: 'Flamewyrm', level: 18, type: 'Fire' },
  { name: 'Aquaflow', level: 16, type: 'Water' },
  { name: 'Thunderbeast', level: 14, type: 'Electric' },
  { name: 'Earthshaker', level: 12, type: 'Ground' },
];

export function CritterTeamOverview() {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-green-800 mb-4">Critter Team Overview</h3>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        {mockTeam.map(critter => <CritterCard key={critter.name} {...critter} />)}
      </div>
    </div>
  );
}