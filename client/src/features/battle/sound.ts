/**
 * Short blips synthesised in the browser. The arena had no sound at all beyond
 * the background track, and three effects of about 150ms each are not worth
 * shipping audio files for.
 *
 * Muting is shared with the music control in the battle header, so one toggle
 * silences everything.
 */
type Blip = {
  type: OscillatorType;
  frequency: number;
  endFrequency: number;
  durationMs: number;
  gain: number;
};

const blips = {
  select: { type: 'square', frequency: 660, endFrequency: 880, durationMs: 70, gain: 0.05 },
  hit: { type: 'square', frequency: 320, endFrequency: 120, durationMs: 140, gain: 0.09 },
  superEffective: { type: 'sawtooth', frequency: 520, endFrequency: 90, durationMs: 220, gain: 0.11 },
  faint: { type: 'triangle', frequency: 300, endFrequency: 60, durationMs: 420, gain: 0.09 },
} satisfies Record<string, Blip>;

export type BattleSound = keyof typeof blips;

let audioContext: AudioContext | null = null;
let muted = true;

export function setBattleSoundMuted(next: boolean) {
  muted = next;
}

export function playBattleSound(name: BattleSound) {
  if (muted) return;

  // Browsers only allow an AudioContext once the page has been interacted with,
  // which unmuting always is.
  audioContext ??= new AudioContext();

  const blip = blips[name];
  const startsAt = audioContext.currentTime;
  const endsAt = startsAt + blip.durationMs / 1000;

  const oscillator = audioContext.createOscillator();
  oscillator.type = blip.type;
  oscillator.frequency.setValueAtTime(blip.frequency, startsAt);
  oscillator.frequency.exponentialRampToValueAtTime(blip.endFrequency, endsAt);

  const amplitude = audioContext.createGain();
  amplitude.gain.setValueAtTime(blip.gain, startsAt);
  amplitude.gain.exponentialRampToValueAtTime(0.0001, endsAt);

  oscillator.connect(amplitude).connect(audioContext.destination);
  oscillator.start(startsAt);
  oscillator.stop(endsAt);
}
