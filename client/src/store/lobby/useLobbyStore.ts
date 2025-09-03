import { create } from 'zustand';
import { Client, type IFrame, type IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { LobbyState } from '@store/lobby/types';

export const useLobbyStore = create<LobbyState>((set, get) => ({
  stompClient: null,
  isConnected: false,

  connect: () => {
    if (get().isConnected || get().stompClient?.active) return;

    const token = useAuthStore.getState().token;
    if (!token) return console.error('LobbyStore: No auth token found.');

    const client = new Client({
      webSocketFactory: () => {
        const url = import.meta.env.VITE_LOBBY_SERVICE_URL || 'http://localhost:8081/ws';
        return new SockJS(url);
      },
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        set({ stompClient: client, isConnected: true });
        console.log('Connected to Lobby WebSocket!');
      },
      onDisconnect: () => {
        set({ stompClient: null, isConnected: false });
        console.log('Disconnected from Lobby WebSocket.');
      },
      onStompError: (frame: IFrame) => {
        console.error('Broker error:', frame.headers['message'], frame.body);
      },
    });

    client.activate();
  },

  disconnect: () => {
    get().stompClient?.deactivate();
  },

  subscribe: (topic, callback) => {
    return get().stompClient?.subscribe(topic, (message: IMessage) => {
      callback(JSON.parse(message.body));
    });
  },

  publish: (destination, body) => {
    get().stompClient?.publish({ destination, body: JSON.stringify(body) });
  },
}));