package com.chronocritters.lobby.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chronocritters.lib.util.ServiceAuth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The battle update callback is only ever called by the gamelogic service.
 * Without this, anything on the network could broadcast an arbitrary battle
 * state to both players.
 */
@Component
public class ServiceAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAuthFilter.class);

    private final String internalToken;

    public ServiceAuthFilter(@Value("${services.internal.token}") String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/battle/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!internalToken.equals(request.getHeader(ServiceAuth.HEADER))) {
            logger.warn("Rejected unauthenticated service call to '{}'", request.getRequestURI());
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
