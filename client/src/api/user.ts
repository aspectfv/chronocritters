import { gql } from '@apollo/client';
import client from './apollo';
import type { LoginCredentials, LoginResponse, RegisterCredentials } from '@store/auth/types';

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

export const login = (credentials: LoginCredentials) => {
  return client.mutate<{ login: LoginResponse }>({
    mutation: LOGIN_MUTATION,
    variables: {
      username: credentials.username,
      password: credentials.password,
    },
  });
};

export const register = (credentials: RegisterCredentials) => {
  return client.mutate<{ register: LoginResponse }>({
    mutation: REGISTER_MUTATION,
    variables: {
      username: credentials.username,
      password: credentials.password,
    },
  });
};