import { useEffect, useState } from 'react';
import type { OpponentStatusBannerProps } from '@features/battle/types';

export function OpponentStatusBanner({ opponentName, secondsRemaining }: OpponentStatusBannerProps) {
  const [countdown, setCountdown] = useState(secondsRemaining);

  useEffect(() => {
    setCountdown(secondsRemaining);

    const tick = setInterval(() => {
      setCountdown((remaining) => Math.max(0, remaining - 1));
    }, 1000);

    return () => clearInterval(tick);
  }, [secondsRemaining]);

  return (
    <div
      role="status"
      className="mt-4 flex items-center justify-center gap-3 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800"
    >
      <span className="h-3 w-3 animate-pulse rounded-full bg-amber-500" aria-hidden="true"></span>
      <span>
        <strong>{opponentName}</strong> lost connection. Waiting {countdown}s for them to return before the
        battle is awarded to you.
      </span>
    </div>
  );
}
