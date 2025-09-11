import { gql } from '@apollo/client';
import client from './apollo';
import type { LoginCredentials, LoginResponse, RegisterCredentials } from '@store/auth/types';
import type { GetPlayerStatsData, GetPlayerStatsVars } from '@features/menu/types';
import type { GetMyCrittersData, GetMyCrittersVars, GetPlayerOverviewData, GetPlayerOverviewVars } from '@features/profile/types';

const LOGIN_MUTATION = gql`
  mutation Login($username: String!, $password: String!) {
    login(username: $username, password: $password) {
      user {
        id
        username
      }
      token
    }
  }
`;

export const login = (credentials: LoginCredentials) => {
  return client.mutate<{ login: LoginResponse }>({
    mutation: LOGIN_MUTATION,
    variables: {
      username: credentials.username,
      password: credentials.password,
    },
  });
};

const REGISTER_MUTATION = gql`
  mutation Register($username: String!, $password: String!) {
    register(username: $username, password: $password) {
      user {
        id
        username
      }
      token
    }
  }
`;

export const register = (credentials: RegisterCredentials) => {
  return client.mutate<{ register: LoginResponse }>({
    mutation: REGISTER_MUTATION,
    variables: {
      username: credentials.username,
      password: credentials.password,
    },
  });
};

const GET_PLAYER_STATS_QUERY = gql`
  query GetPlayerStats($id: ID!) {
    getPlayer(id: $id) {
      stats {
        wins
        losses
      }
    }
  }
`;

export const getPlayerStats = async (userId: string) => {
  const { data } = await client.query<GetPlayerStatsData, GetPlayerStatsVars>({
    query: GET_PLAYER_STATS_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });

  return data?.getPlayer.stats ?? null;
};

const GET_PLAYER_OVERVIEW_QUERY = gql`
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
        type
      }
    }
  }
`;

export const getPlayerOverview = async (userId: string) => {
  const { data } = await client.query<GetPlayerOverviewData, GetPlayerOverviewVars>({
    query: GET_PLAYER_OVERVIEW_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });

  return data?.getPlayer ?? null;
};

const GET_MY_CRITTERS_QUERY = gql`
  query GetMyCritters($id: ID!) {
    getPlayer(id: $id) {
      roster {
        id
        name
        type
        baseStats {
          health
          attack
          defense
        }
        abilities {
          id
          name
          effects {
            ... on DamageEffect {
              id
              type
              damage
            }
            ... on DamageOverTimeEffect {
              id
              type
              damagePerTurn
              duration
            }
            ... on SkipTurnEffect {
              id
              type
              duration
            }
          }
        }
      }
    }
  }
`;

export const getMyCritters = async (userId: string) => {
  const { data } = await client.query<GetMyCrittersData, GetMyCrittersVars>({
    query: GET_MY_CRITTERS_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });

  return data?.getPlayer?.roster ?? null;
};