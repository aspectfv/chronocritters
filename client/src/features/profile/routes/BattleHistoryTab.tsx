import { Link } from 'react-router-dom';

const mockHistory = [
  { id: '1', result: 'Win', opponent: 'StormCaller', critter: 'Flamewyrm', date: '2024-01-15' },
  { id: '2', result: 'Win', opponent: 'AquaMaster', critter: 'Thunderbeast', date: '2024-01-14' },
  { id: '3', result: 'Loss', opponent: 'RockCrusher', critter: 'Aquaflow', date: '2024-01-13' },
  { id: '4', result: 'Win', opponent: 'FireLord', critter: 'Earthshaker', date: '2024-01-12' },
  { id: '5', result: 'Win', opponent: 'WindWalker', critter: 'Flamewyrm', date: '2024-01-11' },
];

export function BattleHistoryTab() {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 className="font-semibold text-green-800 mb-4">Recent Battle History</h3>
      <div className="space-y-3">
        {mockHistory.map((item, index) => (
          <Link to={`/profile/history/${item.id}`} key={index} className="block p-4 rounded-lg border border-gray-200 bg-gray-50 hover:bg-gray-100 transition-colors">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <span className={`px-3 py-1 text-xs font-bold rounded-full ${item.result === 'Win' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                  {item.result}
                </span>
                <div>
                  <p className="font-bold text-gray-800">vs {item.opponent}</p>
                  <p className="text-sm text-gray-500">Used: {item.critter}</p>
                </div>
              </div>
              <p className="text-sm text-gray-500">{item.date}</p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}