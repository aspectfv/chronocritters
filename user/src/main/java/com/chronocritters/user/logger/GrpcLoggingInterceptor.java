package com.chronocritters.user.logger;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcGlobalServerInterceptor
public class GrpcLoggingInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GrpcLoggingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        String methodName = call.getMethodDescriptor().getFullMethodName();
        logger.info("gRPC Request Started: {}", methodName);

        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                if (status.isOk()) {
                    logger.info("gRPC Request Succeeded: {}", methodName);
                } else {
                    logger.error("gRPC Request Failed: {}, Status: {}, Description: {}",
                            methodName,
                            status.getCode(),
                            status.getDescription());
                }
                super.close(status, trailers);
            }
        };
        
        return next.startCall(wrappedCall, headers);
    }
}