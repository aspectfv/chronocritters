package com.chronocritters.user.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQlLoggingInterceptor implements WebGraphQlInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GraphQlLoggingInterceptor.class);

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> variables = request.getVariables();

        String safeVariables = variables.entrySet().stream()
                .map(entry -> {
                    if (entry.getKey().toLowerCase().contains("password")) {
                        return entry.getKey() + "=[PROTECTED]";
                    }
                    return entry.getKey() + "=" + entry.getValue();
                })
                .collect(Collectors.joining(", ", "{", "}"));

        logger.info("GraphQL Request Started: operationName='{}', variables={}",
                request.getOperationName(), safeVariables);

        return chain.next(request).doOnSuccess(response -> {
            long duration = System.currentTimeMillis() - startTime;
            if (response.isValid()) {
                logger.info("GraphQL Request Succeeded: operationName='{}', duration={}ms",
                        request.getOperationName(), duration);
            } else {
                logger.warn("GraphQL Request Completed with Errors: operationName='{}', errors={}, duration={}ms",
                        request.getOperationName(), response.getErrors(), duration);
            }
        }).doOnError(exception -> {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("GraphQL Request Failed: operationName='{}', error='{}', duration={}ms",
                    request.getOperationName(), exception.getMessage(), duration, exception);
        });
    }
}