import { ApolloClient, HttpLink, InMemoryCache } from '@apollo/client';
import { SetContextLink } from '@apollo/client/link/context';
import { useAuthStore } from '@store/auth/useAuthStore';

const httpLink = new HttpLink({
  uri: `${import.meta.env.VITE_USER_SERVICE_URL || 'http://localhost:8080'}/graphql`,
});

const authLink = new SetContextLink((prevContext, _) => {
  const token = useAuthStore.getState().token;

  return {
    headers: {
      ...prevContext,
      authorization: token ? `Bearer ${token}` : '',
    },
  };
});

const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache(),
});

export default client;