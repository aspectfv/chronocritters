export type Result = 'victory' | 'defeat' | null;
export interface ResultsHeaderProps {
  result: Result;
  opponentName: string;
}
