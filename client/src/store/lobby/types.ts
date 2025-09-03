import type { Client, StompSubscription } from '@stomp/stompjs';

export interface LobbyState {
  stompClient: Client | null;
  isConnected: boolean;
  connect: () => void;
  disconnect: () => void;
  subscribe: (topic: string, callback: (message: any) => void) => StompSubscription | undefined;
  publish: (destination: string, body: object) => void;
}