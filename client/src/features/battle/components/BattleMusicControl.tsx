import { useEffect, useRef, useState } from "react";
import { setBattleSoundMuted } from "@features/battle/sound";

export function BattleMusicControl() {
  const audioRef = useRef<HTMLAudioElement>(null);
  const [isMuted, setIsMuted] = useState(true);
  const [volume, setVolume] = useState(0.5);

  useEffect(() => {
    // One toggle covers the music and the battle effects.
    setBattleSoundMuted(isMuted);

    if (audioRef.current) {
      audioRef.current.muted = isMuted;
      if (!isMuted) {
        audioRef.current.play().catch(() => {});
      } else {
        audioRef.current.pause();
      }
    }
  }, [isMuted]);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  useEffect(() => {
    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
    };
  }, []);

  return (
    <>
      <audio
        ref={audioRef}
        src="/battle.mp3"
        loop
        autoPlay={false}
        style={{ display: "none" }}
        muted={isMuted}
      />
      <div className="flex items-center gap-2">
        <button
          onClick={() => setIsMuted((prev) => !prev)}
          className="flex h-8 w-8 items-center justify-center rounded-full text-arena-ink-muted transition-colors hover:bg-arena-glass hover:text-arena-ink focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass"
          aria-label={isMuted ? "Unmute Music" : "Mute Music"}
        >
          {isMuted ? (
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 9l6 6M15 9l-6 6" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5v14l-7-7h4a1 1 0 001-1V8a1 1 0 00-1-1H4l7-7" />
            </svg>
          ) : (
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-brass" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5v14l-7-7h4a1 1 0 001-1V8a1 1 0 00-1-1H4l7-7" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10a3 3 0 010 4" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8a7 7 0 010 8" />
            </svg>
          )}
        </button>
        <input
          type="range"
          min={0}
          max={1}
          step={0.01}
          value={isMuted ? 0 : volume}
          onChange={e => setVolume(Number(e.target.value))}
          className="hidden h-2 w-16 accent-brass lg:block"
          disabled={isMuted}
        />
        <span className="hidden w-7 text-center text-xs text-arena-ink-muted lg:block">{!isMuted ? Math.round(volume * 100) : 0}</span>
      </div>
    </>
  );
}