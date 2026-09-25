import type { CritterType, GetCritterCatalogQuery } from '@/gql/graphql';

export type CatalogCritter = GetCritterCatalogQuery['critters'][number];
export type TypeAdvantage = GetCritterCatalogQuery['typeAdvantages'][number];

export interface CatalogEntryProps {
  critter: CatalogCritter;
  /** The highest value in the catalogue for each stat, so the meters compare. */
  ceilings: { health: number; attack: number; defense: number };
}

export interface TypeWheelProps {
  advantages: TypeAdvantage[];
  /** Only the types something in the catalogue actually has. */
  types: CritterType[];
}
