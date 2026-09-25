import { useEffect, useRef, useState } from "react";
import { Volume2, VolumeX } from "lucide-react";
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

  const level = isMuted ? 0 : Math.round(volume * 100);

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
          type="button"
          onClick={() => setIsMuted((prev) => !prev)}
          className={`key flex h-8 w-8 items-center justify-center rounded-md focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 ${
            isMuted ? 'bg-arena text-arena-ink-muted' : 'bg-brass text-white'
          }`}
          aria-label={isMuted ? "Turn sound on" : "Turn sound off"}
          aria-pressed={!isMuted}
        >
          {isMuted
            ? <VolumeX className="h-4 w-4" aria-hidden="true" />
            : <Volume2 className="h-4 w-4" aria-hidden="true" />}
        </button>

        <input
          type="range"
          min={0}
          max={1}
          step={0.01}
          value={isMuted ? 0 : volume}
          onChange={e => setVolume(Number(e.target.value))}
          style={{ '--fill': `${level}%` } as React.CSSProperties}
          className="slider hidden w-20 lg:block"
          disabled={isMuted}
          aria-label="Volume"
        />

        <span className="numeral hidden w-8 text-center text-xs text-arena-ink lg:block">{level}</span>
      </div>
    </>
  );
}
