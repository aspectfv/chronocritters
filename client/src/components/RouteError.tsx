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
    <main className="min-h-screen bg-canvas flex items-center justify-center p-4">
      <div className="w-full max-w-md rounded-xl border border-line bg-surface p-8 text-center shadow-sm">
        <h1 className="text-2xl font-bold text-accent-ink">{heading}</h1>
        <p className="mt-2 text-ink-muted">{detail}</p>
        <div className="mt-6 flex flex-col gap-3 sm:flex-row sm:justify-center">
          <button
            type="button"
            onClick={() => window.location.reload()}
            className="rounded-lg bg-accent px-6 py-3 font-semibold text-white transition-colors hover:bg-accent-strong focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent-ring focus-visible:ring-offset-2"
          >
            Try again
          </button>
          <Link
            to="/menu"
            className="rounded-lg border border-line-strong bg-surface px-6 py-3 font-semibold text-ink transition-colors hover:bg-surface-sunk focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent-ring focus-visible:ring-offset-2"
          >
            Back to Menu
          </Link>
        </div>
      </div>
    </main>
  );
}
