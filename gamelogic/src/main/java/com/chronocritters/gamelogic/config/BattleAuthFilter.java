package com.chronocritters.gamelogic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.chronocritters.lib.util.JwtUtil;
import com.chronocritters.lib.util.ServiceAuth;

import reactor.core.publisher.Mono;

/**
 * Guards the battle API.
 *
 * Player-driven endpoints require a JWT and expose the authenticated player id
 * as the {@link #PLAYER_ID_ATTRIBUTE} request attribute, so a caller can never
 * act on behalf of their opponent. The remaining endpoints are only ever invoked
 * by the lobby service and require the shared service token instead.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class BattleAuthFilter implements WebFilter {
    public static final String PLAYER_ID_ATTRIBUTE = "playerId";

    private static final String BATTLE_PATH_PREFIX = "/battle/";
    private static final Logger logger = LoggerFactory.getLogger(BattleAuthFilter.class);

    private final String internalToken;

    public BattleAuthFilter(@Value("${services.internal.token}") String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (!path.startsWith(BATTLE_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // Browsers never attach credentials to a CORS preflight. Authenticating one would
        // fail it, and the browser would then block the real request before it is ever sent.
        if (CorsUtils.isPreFlightRequest(request)) {
            return chain.filter(exchange);
        }

        if (isServiceToServiceCall(request, path)) {
            if (!internalToken.equals(request.getHeaders().getFirst(ServiceAuth.HEADER))) {
                return reject(exchange, "This endpoint is only callable by the lobby service");
            }
            return chain.filter(exchange);
        }

        try {
            String playerId = JwtUtil.playerIdFromAuthHeader(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
            exchange.getAttributes().put(PLAYER_ID_ATTRIBUTE, playerId);
        } catch (Exception e) {
            return reject(exchange, e.getMessage());
        }

        return chain.filter(exchange);
    }

    /** {@code POST /battle/{id}}, {@code /timeout} and {@code /forfeit} are driven by the lobby, not by a player. */
    private boolean isServiceToServiceCall(ServerHttpRequest request, String path) {
        if (path.endsWith("/timeout") || path.endsWith("/forfeit")) {
            return true;
        }
        boolean isBattleRoot = path.indexOf('/', BATTLE_PATH_PREFIX.length()) < 0;
        return isBattleRoot && HttpMethod.POST.equals(request.getMethod());
    }

    private Mono<Void> reject(ServerWebExchange exchange, String reason) {
        logger.warn("Rejected request to '{}': {}", exchange.getRequest().getPath().value(), reason);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
