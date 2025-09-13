import client from './apollo';
import type { LoginCredentials, RegisterCredentials } from '@store/auth/types';
import { gql } from '@apollo/client';

import type { 
  GetMyCrittersQuery, 
  GetPlayerOverviewQuery, 
  GetPlayerStatsQuery, 
  LoginMutation, 
  RegisterMutation 
} from '@/gql/graphql';

const LOGIN_MUTATION = gql(`
  mutation Login($username: String!, $password: String!) {
    login(username: $username, password: $password) {
      user {
        id
        username
      }
      token
    }
  }
`);

export const login = (credentials: LoginCredentials) => {
  return client.mutate<LoginMutation>({
    mutation: LOGIN_MUTATION,
    variables: {
      username: credentials.username,
      password: credentials.password,
    },
  });
};

const REGISTER_MUTATION = gql(`
  mutation Register($username: String!, $password: String!) {
    register(username: $username, password: $password) {
      user {
        id
        username
      }
      token
    }
  }
`);

export const register = (credentials: RegisterCredentials) => {
  return client.mutate<RegisterMutation>({
    mutation: REGISTER_MUTATION,
    variables: {
      username: credentials.username,
      password: credentials.password,
    },
  });
};

const GET_PLAYER_STATS_QUERY = gql(`
  query GetPlayerStats($id: ID!) {
    getPlayer(id: $id) {
      stats {
        wins
        losses
      }
    }
  }
`);

export const getPlayerStats = async (userId: string) => {
  const response = await client.query<GetPlayerStatsQuery>({
    query: GET_PLAYER_STATS_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });
  return response.data;
};

const GET_PLAYER_OVERVIEW_QUERY = gql(`
  query GetPlayerOverview($id: ID!) {
    getPlayer(id: $id) {
      id
      username
      stats {
        wins
        losses
      }
      roster {
        name
        description
        type
      }
    }
  }
`);

export const getPlayerOverview = async (userId: string) => {
  const response = await client.query<GetPlayerOverviewQuery>({
    query: GET_PLAYER_OVERVIEW_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });
  return response.data;
};

const GET_MY_CRITTERS_QUERY = gql(`
  query GetMyCritters($id: ID!) {
    getPlayer(id: $id) {
      roster {
        id
        name
        description
        type
        baseStats {
          health
          attack
          defense
        }
        abilities {
          id
          name
          description
          effects {
            ... on DamageEffect {
              id
              description
              damage
            }
            ... on DamageOverTimeEffect {
              id
              description
              damagePerTurn
              duration
            }
            ... on SkipTurnEffect {
              id
              description
              duration
            }
          }
        }
      }
    }
  }
`);

export const getMyCritters = async (userId: string) => {
  const response = await client.query<GetMyCrittersQuery>({
    query: GET_MY_CRITTERS_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });
  return response.data;
};