import type { ProfileTab } from '../routes/ProfilePage';

interface ProfileTabsProps {
  activeTab: ProfileTab;
  setActiveTab: (tab: ProfileTab) => void;
}

const tabs: ProfileTab[] = ['Overview', 'My Critters', 'Battle History'];

export function ProfileTabs({ activeTab, setActiveTab }: ProfileTabsProps) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-2 flex justify-center space-x-2">
      {tabs.map((tab) => (
        <button
          key={tab}
          onClick={() => setActiveTab(tab)}
          className={`px-6 py-2 rounded-md font-semibold text-sm transition-colors ${
            activeTab === tab
              ? 'bg-green-600 text-white shadow-md'
              : 'text-gray-600 hover:bg-green-50'
          }`}
        >
          {tab}
        </button>
      ))}
    </div>
  );
}