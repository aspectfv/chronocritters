import { NavLink } from 'react-router-dom';

export function ProfileTabs() {
  const tabs = [
    { name: 'Overview', path: '/profile' },
    { name: 'My Critters', path: '/profile/critters' },
    { name: 'Battle History', path: '/profile/history' },
  ];

  const activeClass = 'bg-accent text-white shadow-md';
  const inactiveClass = 'text-ink-muted hover:bg-accent-soft';

  return (
    <div className="bg-surface rounded-lg shadow-sm border border-line flex gap-1 overflow-x-auto p-1.5 sm:justify-center sm:gap-2 sm:p-2">
      {tabs.map((tab) => (
        <NavLink
          key={tab.name}
          to={tab.path}
          // end prop important for root tab to prevent it from matching all nested routes
          end={tab.path === '/profile'}
          className={({ isActive }) =>
            `shrink-0 whitespace-nowrap rounded-md px-4 py-2 sm:px-6 font-semibold text-sm transition-colors ${
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