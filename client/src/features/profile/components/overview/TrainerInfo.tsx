import type { TrainerInfoProps } from "@features/profile/types";

export function TrainerInfo({ username, experience, level }: TrainerInfoProps) {
  return (
    <div className="bg-surface rounded-xl shadow-sm border border-line p-6 h-full">
      <div className="flex items-center gap-3 mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-accent-ink" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <h3 className="font-semibold text-lg text-ink">Trainer Info</h3>
      </div>
      <div className="text-center">
        <div className="w-24 h-24 bg-accent-soft rounded-full mx-auto flex items-center justify-center mb-2">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-accent" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
            <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
          </svg>
        </div>
        <p className="text-2xl font-bold text-ink">{username ? username : 'Unknown Trainer'}</p>
      </div>
      <div className="mt-8 space-y-4">
        <div className="flex justify-between items-center text-base">
          <span className="text-ink-muted">Level</span>
          <span className="font-bold text-ink">{level}</span>
        </div>
        <div className="flex justify-between items-center text-base">
          <span className="text-ink-muted">Experience</span>
          <span className="font-bold text-ink">{experience} XP</span>
        </div>
      </div>
    </div>
  );
}