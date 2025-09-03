import { AppRouter } from '@routes/index';
import { LobbyConnectionManager } from '@components/lobby/LobbyConnectionManager';
import { ApolloProvider } from '@apollo/client/react';
import client from './api/apollo';

function App() {
  return (
    <ApolloProvider client={client}>
      <LobbyConnectionManager />
      <AppRouter />
    </ApolloProvider>
  );
}

export default App;