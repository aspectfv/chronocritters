import type { GetMyCrittersQuery, GetPlayerOverviewQuery } from 'src/gql/graphql';

type ArrayElement<ArrayType extends readonly unknown[]> = 
  ArrayType extends readonly (infer ElementType)[] ? ElementType : never;

export type CritterData = NonNullable<ArrayElement<NonNullable<NonNullable<GetMyCrittersQuery['getPlayer']>['roster']>>>;
export type PlayerOverviewData = NonNullable<GetPlayerOverviewQuery['getPlayer']>;

export interface CritterCardProps {
  name: string;
  level: number;
}

export interface CritterListProps {
  roster: CritterData[];
  selectedCritter: CritterData | null;
  onCritterSelect: (critter: CritterData) => void;
}

export interface TrainerInfoProps {
  experience: number;
  level: number;
  username: string;
}

export interface BattleStatisticsProps {
  wins: number;
  losses: number;
}

export interface CritterTeamOverviewProps {
  roster: PlayerOverviewData['roster'];
}