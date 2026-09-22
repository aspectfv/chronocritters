package com.chronocritters.lib.util;

/**
 * Shared header used to authenticate service-to-service calls between the
 * backend services. These endpoints are also blocked at the reverse proxy; the
 * header is the second line of defence for anything already on the network.
 */
public final class ServiceAuth {
    public static final String HEADER = "X-Service-Auth";

    private ServiceAuth() {
        throw new UnsupportedOperationException("Utility class");
    }
}
