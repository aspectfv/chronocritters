import { Outlet } from 'react-router-dom';

function AuthPage() {
  return (
    <main className="min-h-screen bg-arena p-4 text-arena-ink">
      <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center gap-3">
        {/* The sign-in door stands on the same ground as the arena behind it,
            so the game starts at the first screen rather than after it. */}
        <div className="arena-plate relative overflow-hidden rounded-2xl px-5 py-6">
          <div className="arena-floor pointer-events-none absolute inset-0" aria-hidden="true" />
          <p className="display relative text-center text-3xl leading-none text-arena-ink">
            Chrono Critters
          </p>
          <p className="relative mt-1.5 text-center text-sm text-arena-ink-muted">
            Turn-based critter battles, one move at a time.
          </p>
        </div>

        <div className="panel rounded-lg bg-arena-deep p-5">
          <Outlet />
        </div>
      </div>
    </main>
  );
}

export default AuthPage;
