import { NavLink } from 'react-router-dom';

export function ProfileTabs() {
  const tabs = [
    { name: 'Overview', path: '/profile' },
    { name: 'My Critters', path: '/profile/critters' },
    { name: 'Battle History', path: '/profile/history' },
  ];

  const activeClass = 'bg-green-600 text-white shadow-md';
  const inactiveClass = 'text-gray-600 hover:bg-green-50';

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-2 flex justify-center space-x-2">
      {tabs.map((tab) => (
        <NavLink
          key={tab.name}
          to={tab.path}
          // end prop important for root tab to prevent it from matching all nested routes
          end={tab.path === '/profile'}
          className={({ isActive }) =>
            `px-6 py-2 rounded-md font-semibold text-sm transition-colors ${
              isActive ? activeClass : inactiveClass
            }`
          }
        >
          {tab.name}
        </NavLink>
      ))}
    </div>
  );
}