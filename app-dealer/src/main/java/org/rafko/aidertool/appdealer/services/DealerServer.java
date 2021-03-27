package org.rafko.aidertool.appdealer.services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.rafko.AiderTool.RequestDealer;
import org.rafko.AiderTool.RequestHandlerGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DealerServer {
    private static final Logger LOGGER = Logger.getLogger(DealerServer.class.getName());
    private Server server;

    public void start(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RequestHandlerImpl())
                .build()
                .start();
        LOGGER.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                LOGGER.log(Level.WARNING,"*** shutting down gRPC server since JVM is shutting down");
                try {
                    DealerServer.this.stop();
                } catch (InterruptedException e) {

                }
                LOGGER.log(Level.WARNING,"*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }else{
            throw new InterruptedException("Dealer server is null, when stop called!");
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private static class RequestHandlerImpl extends RequestHandlerGrpc.RequestHandlerImplBase {
        @Override
        public void addRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"addRequest call received from : " + request.getRequesterUUID() + "!");
        }

        @Override
        public void cancelRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"addRequest call received!");
        }

        @Override
        public void queryRequest(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"cancelRequest call received!");
        }

        @Override
        public void queryRequests(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"queryRequests call received!");
        }

        @Override
        public void initiate(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"initiate call received!");
        }

        @Override
        public void finalize(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"finalize call received!");
        }

        @Override
        public void ping(RequestDealer.AidRequest request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"ping received from : " + request.getRequesterUUID() + "!");
            responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestState.STATE_REQUEST_OK).build());
            responseObserver.onCompleted();
        }
    }

}
