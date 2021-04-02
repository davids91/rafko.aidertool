package org.rafko.aidertool.appdealer.services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.RequestHandlerGrpc;
import org.rafko.aidertool.appdealer.models.DealerStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DealerServer {
    private static final Logger LOGGER = Logger.getLogger(DealerServer.class.getName());
    private final ListProperty<RequestDealer.AidRequest> requests;
    private final DealerStats stats;
    private Server server;

    public DealerServer(DealerStats stats_){
        stats = stats_;
        requests = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    }

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

    private class RequestHandlerImpl extends RequestHandlerGrpc.RequestHandlerImplBase {
        @Override
        public void addRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            if(!request.getRequesterUUID().isEmpty()) {
                LOGGER.log(Level.INFO, "addRequest call received from : " + request.getRequesterUUID() + "!");
                for (String tag : request.getTagsList()) { /* Add the requested tags into the stored ones */
                    if (!stats.getTagsProperty().contains(tag)) {
                        stats.getTagsProperty().add(tag);
                        LOGGER.log(Level.INFO,"Adding tag: " + tag);
                    }
                }
                requests.add(
                    RequestDealer.AidRequest.newBuilder(request)
                    .setState(RequestDealer.RequestState.STATE_OPEN)
                    .build()
                );
                responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestState.STATE_REQUEST_OK).build());
            }else{
                responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestState.STATE_REQUEST_REJECTED).build());
            }

            responseObserver.onCompleted();
        }

        @Override
        public void cancelRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"addRequest call received from : " + request.getRequesterUUID() + "!");
        }

        @Override
        public void queryRequest(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"cancelRequest call received from : " + request.getUserUUID() + "!");
        }

        @Override
        public void queryRequests(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidRequest> responseObserver) {
            LOGGER.log(Level.INFO,"queryRequests call received from : " + request.getUserUUID() + "!");
            for(RequestDealer.AidRequest storedRequest : requests){
                StringBuilder tagsSummary = new StringBuilder();
                for(String tag : storedRequest.getTagsList()) tagsSummary.append(tag).append(", ");
                LOGGER.log(Level.INFO,"Providing request[of "+ storedRequest.getRequesterUUID() + "] with tags: " + tagsSummary.toString());
                responseObserver.onNext(storedRequest);
            }
            responseObserver.onCompleted();
        }

        @Override
        public void queryTags(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.INFO,"Providing tags to: " + request.getUserUUID());
            RequestDealer.AidToken tagsContainer = RequestDealer.AidToken.newBuilder()
            .addAllTags(stats.getTagsProperty())
            .build();
            responseObserver.onNext(tagsContainer);
            responseObserver.onCompleted();
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

    public ListProperty<RequestDealer.AidRequest> getRequests(){
        return requests;
    }

}
