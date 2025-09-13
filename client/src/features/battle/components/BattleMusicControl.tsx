import { useEffect, useRef, useState } from "react";

export function BattleMusicControl() {
  const audioRef = useRef<HTMLAudioElement>(null);
  const [isMuted, setIsMuted] = useState(true);
  const [volume, setVolume] = useState(0.5);

  useEffect(() => {
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
      <div className="absolute top-0 right-0 flex items-center gap-3 bg-white rounded-full shadow px-4 py-2 mt-4 mr-4 z-10">
        <button
          onClick={() => setIsMuted((prev) => !prev)}
          className="w-8 h-8 flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 border border-gray-300 transition"
          aria-label={isMuted ? "Unmute Music" : "Mute Music"}
        >
          {isMuted ? (
            <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 9l6 6M15 9l-6 6" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5v14l-7-7h4a1 1 0 001-1V8a1 1 0 00-1-1H4l7-7" />
            </svg>
          ) : (
            <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-green-700" fill="none" viewBox="0 0 24 24" stroke="currentColor">
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
          className="w-24 h-2 accent-green-700"
          disabled={isMuted}
        />
        <span className="text-sm font-semibold text-gray-700 w-8 text-center">{!isMuted ? Math.round(volume * 100) : 0}</span>
      </div>
    </>
  );
}