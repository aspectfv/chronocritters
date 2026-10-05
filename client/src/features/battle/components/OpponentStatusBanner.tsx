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
      className="flex items-center justify-center gap-3 rounded-lg border border-warn/40 bg-warn/15 px-4 py-2 text-sm text-warn"
    >
      <span className="h-2.5 w-2.5 animate-pulse rounded-full bg-warn" aria-hidden="true"></span>
      <span>
        <strong>{opponentName}</strong> lost connection. Waiting {countdown}s for them to return before the
        battle is awarded to you.
      </span>
    </div>
  );
}
