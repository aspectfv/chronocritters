import { AppRouter } from '@routes/index'
import { LobbyConnectionManager } from '@components/lobby/LobbyConnectionManager'

function App() {
  return (
    <>
      <LobbyConnectionManager />
      <AppRouter />
    </>
  )
}

export default App
