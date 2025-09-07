package com.chronocritters.gamelogic.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Instant startTime = Instant.now();
        ServerHttpRequest request = exchange.getRequest();

        logger.info("Request Started: method={}, uri='{}'",
                request.getMethod(),
                request.getURI());

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long duration = Duration.between(startTime, Instant.now()).toMillis();
                    logger.info("Request Succeeded: status={}, duration={}ms",
                            response.getStatusCode(),
                            duration);
                })
                .doOnError(throwable -> {
                    long duration = Duration.between(startTime, Instant.now()).toMillis();
                    logger.error("Request Failed: error='{}', duration={}ms",
                            throwable.getMessage(),
                            duration,
                            throwable);
                });
    }
}