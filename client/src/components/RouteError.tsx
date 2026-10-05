import { Link, isRouteErrorResponse, useRouteError } from 'react-router-dom';

/**
 * Without this, a loader that throws drops the player onto React Router's
 * developer stack trace, in production.
 */
export function RouteError() {
  const error = useRouteError();

  const status = isRouteErrorResponse(error) ? error.status : null;
  const heading = status === 404 ? 'That page does not exist' : 'Something went wrong';
  const detail = status === 404
    ? 'The link may be out of date.'
    : 'The page could not be loaded. It is usually worth trying again.';

  return (
    <main className="min-h-screen bg-arena p-4 text-arena-ink">
      <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center">
        <div className="panel rounded-lg bg-arena-deep p-6 text-center">
          <h1 className="display text-2xl text-arena-ink">{heading}</h1>
          <p className="mt-2 text-sm text-arena-ink-muted">{detail}</p>

          <div className="mt-5 flex flex-col gap-2.5 sm:flex-row">
            <button
              type="button"
              onClick={() => window.location.reload()}
              className="key flex-1 rounded-lg bg-brass px-4 py-3 font-black text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
            >
              Try again
            </button>
            <Link
              to="/menu"
              className="key flex-1 rounded-lg bg-arena-deep px-4 py-3 text-center font-black text-arena-ink focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
            >
              Back to the menu
            </Link>
          </div>
        </div>
      </div>
    </main>
  );
}
