import { create } from 'zustand';
import { Client, type IFrame, type IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuthStore } from '@store/auth/useAuthStore';
import type { LobbyState } from '@store/lobby/types';

export const useLobbyStore = create<LobbyState>((set, get) => ({
  stompClient: null,
  connectionStatus: 'disconnected',

  connect: () => {
    if (get().stompClient?.active || get().connectionStatus === 'connecting') {
      return;
    }

    set({ connectionStatus: 'connecting' });

    const token = useAuthStore.getState().token;
    if (!token) {
      console.error('LobbyStore: No auth token found.');
      set({ connectionStatus: 'error' });
      return;
    }

    const client = new Client({
      webSocketFactory: () => {
        const url = import.meta.env.VITE_LOBBY_SERVICE_URL || 'http://localhost:8081/ws';
        return new SockJS(url);
      },
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        set({ stompClient: client, connectionStatus: 'connected' });
      },
      onWebSocketClose: () => {
        if (get().connectionStatus !== 'disconnected') {
          set({ connectionStatus: 'disconnected', stompClient: null });
        }
      },
      onDisconnect: () => {
        set({ stompClient: null, connectionStatus: 'disconnected' });
      },
      onStompError: (frame: IFrame) => {
        console.error('Broker error:', frame.headers['message'], frame.body);
        set({ connectionStatus: 'error' });
        get().disconnect();
      },
      reconnectDelay: 5000,
    });

    client.activate();
  },

  disconnect: () => {
    get().stompClient?.deactivate();
    set({ stompClient: null, connectionStatus: 'disconnected' });
  },

  subscribe: (topic, callback) => {
    if (get().connectionStatus !== 'connected') {
      console.warn(`LobbyStore: Attempted to subscribe to '${topic}' while disconnected. Aborting.`);
      return undefined;
    }
    return get().stompClient?.subscribe(topic, (message: IMessage) => {
      callback(JSON.parse(message.body));
    });
  },

  publish: (destination, body) => {
    if (get().connectionStatus !== 'connected') {
      console.warn(`LobbyStore: Attempted to publish to '${destination}' while disconnected. Aborting.`);
      return;
    }
    get().stompClient?.publish({ destination, body: JSON.stringify(body) });
  },
}));