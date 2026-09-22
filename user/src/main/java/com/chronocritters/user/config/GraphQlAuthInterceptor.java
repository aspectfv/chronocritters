package com.chronocritters.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.chronocritters.lib.util.JwtUtil;

import reactor.core.publisher.Mono;

/**
 * Publishes the authenticated player id into the GraphQL context.
 *
 * Authentication is optional here because login and register share the same
 * endpoint; the queries that return player data require the value themselves.
 */
@Component
public class GraphQlAuthInterceptor implements WebGraphQlInterceptor {
    public static final String PLAYER_ID_CONTEXT_KEY = "playerId";

    private static final Logger logger = LoggerFactory.getLogger(GraphQlAuthInterceptor.class);

    @Override
    @NonNull
    public Mono<WebGraphQlResponse> intercept(@NonNull WebGraphQlRequest request, @NonNull Chain chain) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null) {
            try {
                String playerId = JwtUtil.playerIdFromAuthHeader(authorizationHeader);
                request.configureExecutionInput((input, builder) ->
                        builder.graphQLContext(context -> context.put(PLAYER_ID_CONTEXT_KEY, playerId)).build());
            } catch (Exception e) {
                logger.warn("Ignoring invalid Authorization header: {}", e.getMessage());
            }
        }

        return chain.next(request);
    }
}
