package com.chronocritters.lobby.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // The endpoint clients will connect to: ws://<host>/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // sockjs fallback for no websocket support
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
    // Prefixes for messages sent from server to client (subscriptions)
    config.enableSimpleBroker("/topic", "/user"); // /user for unicast, /topic for broadcast
    // Prefix for messages sent from client to server (app destination)
    config.setApplicationDestinationPrefixes("/app");
    // Prefix for user-specific messages (unicast)
    config.setUserDestinationPrefix("/user");
    }
}
