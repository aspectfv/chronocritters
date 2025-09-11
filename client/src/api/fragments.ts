import { graphql } from '../gql';

export const EffectFields = graphql(`
  fragment EffectFields on Effect {
    id
    type
    __typename
    ... on DamageEffect {
      damage
    }
    ... on DamageOverTimeEffect {
      damagePerTurn
      duration
    }
    ... on SkipTurnEffect {
      duration
    }
  }
`);