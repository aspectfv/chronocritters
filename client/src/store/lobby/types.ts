import type { Client, StompSubscription } from '@stomp/stompjs';

export enum ConnectionStatus {
  CONNECTED = 'CONNECTED',
  DISCONNECTED = 'DISCONNECTED',
  CONNECTING = 'CONNECTING',
  ERROR = 'ERROR',
}

export interface LobbyState {
  stompClient: Client | null;
  connectionStatus: ConnectionStatus;
  connect: () => void;
  disconnect: () => void;
  subscribe: (topic: string, callback: (message: any) => void) => StompSubscription | undefined;
  publish: (destination: string, body: object) => void;
}