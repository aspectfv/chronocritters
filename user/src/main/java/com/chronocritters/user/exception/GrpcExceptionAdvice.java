package com.chronocritters.user.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcAdvice
public class GrpcExceptionAdvice {

    private final Logger logger = LoggerFactory.getLogger(GrpcExceptionAdvice.class);

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("gRPC Service call failed with invalid argument: {}", ex.getMessage());
        Status status = Status.INVALID_ARGUMENT.withDescription(ex.getMessage());
        return status.asRuntimeException();
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public StatusRuntimeException handleRuntimeException(RuntimeException ex) {
        logger.error("gRPC Service call failed with an unexpected runtime exception.", ex);
        Status status = Status.UNKNOWN.withDescription("An unexpected error occurred on the server.");
        return status.asRuntimeException();
    }
    
    @GrpcExceptionHandler
    public StatusRuntimeException handleDefault(Exception ex) {
         logger.error("An unhandled exception occurred in gRPC service.", ex);
        Status status = Status.INTERNAL.withDescription("An internal server error occurred.");
        return status.asRuntimeException();
    }
}