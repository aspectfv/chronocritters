/** Shown while a route chunk loads. Reachable now that routes are lazy. */
export function RouteFallback() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-canvas" role="status" aria-live="polite">
      <span className="h-8 w-8 animate-spin rounded-full border-2 border-accent border-t-transparent" aria-hidden="true"></span>
      <span className="sr-only">Loading</span>
    </div>
  );
}
