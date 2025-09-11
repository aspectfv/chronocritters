import client from './apollo';
import type { LoginCredentials, RegisterCredentials } from '@store/auth/types';
import { graphql } from 'src/gql';
import type { 
  GetMyCrittersQuery, 
  GetPlayerOverviewQuery, 
  GetPlayerStatsQuery, 
  LoginMutation, 
  RegisterMutation 
} from 'src/gql/graphql';

const LOGIN_MUTATION = graphql(`
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

const REGISTER_MUTATION = graphql(`
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

const GET_PLAYER_STATS_QUERY = graphql(`
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
  return await client.query<GetPlayerStatsQuery>({
    query: GET_PLAYER_STATS_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });
};

const GET_PLAYER_OVERVIEW_QUERY = graphql(`
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
`);

export const getPlayerOverview = async (userId: string) => {
  return await client.query<GetPlayerOverviewQuery>({
    query: GET_PLAYER_OVERVIEW_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });
};

const GET_MY_CRITTERS_QUERY = graphql(`
  #import "./fragments.ts"
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
            ...EffectFields
          }
        }
      }
    }
  }
`);

export const getMyCritters = async (userId: string) => {
  return await client.query<GetMyCrittersQuery>({
    query: GET_MY_CRITTERS_QUERY,
    variables: { id: userId },
    fetchPolicy: 'network-only'
  });
};