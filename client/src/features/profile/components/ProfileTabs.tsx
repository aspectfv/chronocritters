import { NavLink } from 'react-router-dom';

const tabs = [
  { name: 'Overview', path: '/profile' },
  { name: 'Critters', path: '/profile/critters' },
  { name: 'Battles', path: '/profile/history' },
];

export function ProfileTabs() {
  return (
    <div className="flex gap-2 overflow-x-auto">
      {tabs.map((tab) => (
        <NavLink
          key={tab.name}
          to={tab.path}
          // end prop important for root tab to prevent it from matching all nested routes
          end={tab.path === '/profile'}
          className={({ isActive }) =>
            `key shrink-0 whitespace-nowrap rounded-lg px-5 py-2.5 text-sm font-black focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 ${
              isActive ? 'bg-brass text-white' : 'bg-arena-deep text-arena-ink'
            }`
          }
        >
          {tab.name}
        </NavLink>
      ))}
    </div>
  );
}
