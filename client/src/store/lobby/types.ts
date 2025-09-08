import type { Client, StompSubscription } from '@stomp/stompjs';

export type ConnectionStatus = 'connected' | 'disconnected' | 'connecting' | 'error';

export interface LobbyState {
  stompClient: Client | null;
  connectionStatus: ConnectionStatus;
  connect: () => void;
  disconnect: () => void;
  subscribe: (topic: string, callback: (message: any) => void) => StompSubscription | undefined;
  publish: (destination: string, body: object) => void;
}