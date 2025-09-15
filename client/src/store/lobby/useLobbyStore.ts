import { create } from 'zustand';
import { Client, type IFrame, type IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuthStore } from '@store/auth/useAuthStore';
import { ConnectionStatus, type LobbyState } from '@store/lobby/types';

export const useLobbyStore = create<LobbyState>((set, get) => ({
  stompClient: null,
  connectionStatus: ConnectionStatus.DISCONNECTED,

  connect: () => {
    if (get().stompClient?.active || get().connectionStatus === ConnectionStatus.CONNECTING) {
      return;
    }

    set({ connectionStatus: ConnectionStatus.CONNECTING });

    const client = new Client({
      webSocketFactory: () => {
        const url = import.meta.env.VITE_LOBBY_SERVICE_URL || 'http://localhost:8081/ws';
        return new SockJS(url);
      },
      connectHeaders: {
        get Authorization() {
          return `Bearer ${useAuthStore.getState().token}`;
        }
      },
      onConnect: () => {
        set({ stompClient: client, connectionStatus: ConnectionStatus.CONNECTED });
      },
      onWebSocketClose: () => {
        if (get().connectionStatus !== ConnectionStatus.DISCONNECTED) {
          set({ connectionStatus: ConnectionStatus.DISCONNECTED, stompClient: null });
        }
      },
      onDisconnect: () => {
        set({ stompClient: null, connectionStatus: ConnectionStatus.DISCONNECTED });
      },
      onStompError: (frame: IFrame) => {
        console.error('Broker error:', frame.headers['message'], frame.body);
        set({ connectionStatus: ConnectionStatus.ERROR });
        get().disconnect();
        useAuthStore.getState().logout();
      },
      reconnectDelay: 5000,
    });

    client.activate();
  },

  disconnect: () => {
    get().stompClient?.deactivate();
    set({ stompClient: null, connectionStatus: ConnectionStatus.DISCONNECTED });
  },

  subscribe: (topic, callback) => {
    if (get().connectionStatus !== ConnectionStatus.CONNECTED) {
      console.warn(`LobbyStore: Attempted to subscribe to '${topic}' while disconnected. Aborting.`);
      return undefined;
    }
    return get().stompClient?.subscribe(topic, (message: IMessage) => {
      callback(JSON.parse(message.body));
    });
  },

  publish: (destination, body) => {
    if (get().connectionStatus !== ConnectionStatus.CONNECTED) {
      console.warn(`LobbyStore: Attempted to publish to '${destination}' while disconnected. Aborting.`);
      return;
    }
    get().stompClient?.publish({ destination, body: JSON.stringify(body) });
  },
}));