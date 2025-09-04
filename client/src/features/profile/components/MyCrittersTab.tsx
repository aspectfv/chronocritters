export function MyCrittersTab() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
      <div className="md:col-span-1 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="font-semibold text-green-800 mb-4">My Critters (4)</h3>
        <div className="space-y-3">
          {/* Mock Critter List */}
          <div className="p-4 rounded-lg border border-gray-200 bg-gray-50 cursor-pointer hover:border-green-400">
            <p className="font-bold text-gray-800">🔥 Flamewyrm <span className="float-right text-yellow-500 text-xs">⭐ 83%</span></p>
            <p className="text-sm text-gray-500">Fire &nbsp;&middot;&nbsp; Level 18</p>
          </div>
          <div className="p-4 rounded-lg border border-gray-200 bg-gray-50 cursor-pointer hover:border-green-400">
            <p className="font-bold text-gray-800">💧 Aquaflow <span className="float-right text-yellow-500 text-xs">⭐ 74%</span></p>
            <p className="text-sm text-gray-500">Water &nbsp;&middot;&nbsp; Level 16</p>
          </div>
          <div className="p-4 rounded-lg border border-gray-200 bg-gray-50 cursor-pointer hover:border-green-400">
            <p className="font-bold text-gray-800">⚡ Thunderbeast <span className="float-right text-yellow-500 text-xs">⭐ 65%</span></p>
            <p className="text-sm text-gray-500">Electric &nbsp;&middot;&nbsp; Level 14</p>
          </div>
          <div className="p-4 rounded-lg border border-gray-200 bg-gray-50 cursor-pointer hover:border-green-400">
            <p className="font-bold text-gray-800">🌍 Earthshaker <span className="float-right text-yellow-500 text-xs">⭐ 50%</span></p>
            <p className="text-sm text-gray-500">Ground &nbsp;&middot;&nbsp; Level 12</p>
          </div>
        </div>
      </div>
      <div className="md:col-span-2 bg-white rounded-lg shadow-sm border border-gray-200 p-6 flex flex-col items-center justify-center text-center">
        <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-gray-400" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" /></svg>
        </div>
        <h3 className="font-semibold text-gray-800 text-xl">Select a Critter</h3>
        <p className="text-gray-500">Select a critter from the list to view its details</p>
      </div>
    </div>
  );
}