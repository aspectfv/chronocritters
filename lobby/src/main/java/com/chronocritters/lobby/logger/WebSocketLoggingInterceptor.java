package com.chronocritters.lobby.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketLoggingInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketLoggingInterceptor.class);

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        StompCommand command = accessor.getCommand();
        String destination = accessor.getDestination();

        if (command != null) {
            switch (command) {
                case CONNECT -> 
                    logger.info("CONNECT Received: sessionId='{}'", sessionId);
                case SUBSCRIBE -> 
                    logger.info("SUBSCRIBE Received: sessionId='{}', destination='{}'", sessionId, destination);
                case SEND -> 
                    logger.info("SEND Received: sessionId='{}', destination='{}'", sessionId, destination);
                case DISCONNECT -> 
                    logger.info("DISCONNECT Received: sessionId='{}'", sessionId);
                default -> 
                    logger.debug("Message Received: sessionId='{}', command={}, destination='{}'", sessionId, command.name(), destination);
            }
        } else {
             logger.debug("Message Received with UNKNOWN command: sessionId='{}', destination='{}'", sessionId, destination);
        }
        return message;
    }

    @Override
    public void afterSendCompletion(@NonNull Message<?> message, @NonNull MessageChannel channel, boolean sent, @Nullable Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();

        if (ex != null) {
            logger.error("Message Send Failed: sessionId='{}', destination='{}', error='{}'",
                    sessionId, destination, ex.getMessage(), ex);
        } else if (!sent) {
            logger.warn("Message Send Failed (no exception): sessionId='{}', destination='{}'",
                    sessionId, destination);
        }
    }
}