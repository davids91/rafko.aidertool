package org.rafko.aidertool.appdealer.services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.RequestHandlerGrpc;
import org.rafko.aidertool.appdealer.models.DealerStats;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;
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
        requests.addListener((ListChangeListener<? super RequestDealer.AidRequest>) change -> {
            while(change.next()){
                if(change.wasAdded()){
                    ArrayList<RequestDealer.AidRequest> toRemove = new ArrayList<>();
                    for(RequestDealer.AidRequest newRequest : change.getAddedSubList()){
                        if(
                            (RequestDealer.HelpState.STATE_FINISHED == newRequest.getState())
                            ||(RequestDealer.HelpState.STATE_UNKNOWN == newRequest.getState())
                            ||(RequestDealer.HelpState.STATE_CANCELLED == newRequest.getState())
                        ){
                            toRemove.add(newRequest);
                        }
                    }
                    if(0 < toRemove.size()) requests.removeAll(toRemove);
                }
            }
        });
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
                    LOGGER.log(Level.WARNING, "Interrupted at stop..", e);
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
        public void add(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            if(!request.getRequesterUUID().isEmpty()) {
                LOGGER.log(Level.FINE, "addRequest call received from : " + request.getRequesterUUID() + "!");
                for (String tag : request.getTagsList()) { /* Add the requested tags into the stored ones */
                    if (!stats.getTagsProperty().contains(tag)) {
                        stats.getTagsProperty().add(tag);
                        LOGGER.log(Level.FINE,"Adding tag: " + tag);
                    }
                }
                requests.add(
                    RequestDealer.AidRequest.newBuilder(request)
                    .setState(RequestDealer.HelpState.STATE_OPEN)
                    .setRequestID(UUID.randomUUID().toString())
                    .build()
                );
                responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestResponse.QUERY_OK).build());
            }else{
                responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestResponse.QUERY_REJECTED).build());
            }

            responseObserver.onCompleted();
        }

        @Override
        public void queryRequest(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.FINE,"queryRequest call received from : " + request.getUserUUID() + "!");
        }

        @Override
        public void queryRequestsChanged(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            /* TODO: Implement dirty bit, or remove service */
            LOGGER.log(Level.SEVERE, "Unsupported operation queryRequestChanged!");
        }

        @Override
        public void queryRequests(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidRequest> responseObserver) {
            LOGGER.log(Level.FINE,"queryRequests call received from : " + request.getUserUUID() + "!");
            for(RequestDealer.AidRequest storedRequest : requests){ /* TODO filter for tags */
                StringBuilder tagsSummary = new StringBuilder();
                for(String tag : storedRequest.getTagsList()) tagsSummary.append(tag).append(", ");
                LOGGER.log(Level.INFO,"Providing request[of "+ storedRequest.getRequesterUUID() + "] with tags: " + tagsSummary.toString());
                responseObserver.onNext(storedRequest);
            }
            responseObserver.onCompleted();
        }

        @Override
        public void queryTags(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.FINE,"Providing tags to: " + request.getUserUUID());
            RequestDealer.AidToken tagsContainer = RequestDealer.AidToken.newBuilder()
            .addAllTags(stats.getTagsProperty())
            .build();
            responseObserver.onNext(tagsContainer);
            responseObserver.onCompleted();
        }

        @Override
        public void initiate(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.FINE,"initiate call received!");
            RequestDealer.AidRequest storedRequest = getRequestByID(request.getRequestID());
            if( /* If the given request is found */
                (null != storedRequest)
                &&( /* And can be initiated */
                    ( /* The requester can not initiate its own request when it is open.. */
                        (RequestDealer.HelpState.STATE_OPEN == storedRequest.getState())
                        &&(!storedRequest.getRequesterUUID().equals(request.getUserUUID()))
                    )
                    ||( /* If it's postponed, only the requester can undo it.. */
                        (RequestDealer.HelpState.STATE_POSTPONED == storedRequest.getState())
                        &&(storedRequest.getRequesterUUID().equals(request.getUserUUID()))
                    )
                )
            ){
                switch (storedRequest.getState()){
                    case STATE_OPEN:{
                        RequestDealer.AidRequest newRequest = (
                            RequestDealer.AidRequest.newBuilder(storedRequest)
                            .setState(RequestDealer.HelpState.STATE_ACTIVE)
                            .setHelperUUID(request.getUserUUID())
                            .build()
                        );
                        requests.remove(storedRequest);
                        requests.add(newRequest);
                        responseObserver.onNext(getAcceptedResponse());
                    }break;
                    case STATE_POSTPONED:{
                        RequestDealer.AidRequest newRequest = (
                            RequestDealer.AidRequest.newBuilder(storedRequest)
                            .setState(RequestDealer.HelpState.STATE_OPEN)
                            .build()
                        );
                        requests.remove(storedRequest);
                        requests.add(newRequest);
                        responseObserver.onNext(getAcceptedResponse());
                    }break;
                    default: responseObserver.onNext(getRejectedResponse());
                }
            }
            responseObserver.onCompleted();
        }

        @Override
        public void cancel(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.FINE,"cancel request call received from : " + request.getUserUUID() + "!");
            RequestDealer.AidRequest storedRequest = getRequestByID(request.getRequestID());
            if( /* The request is found */
                (null != storedRequest)
                &&(
                    ( /* Only the requester can remove its request */
                        ( /* In both pending and open states, the requester can cancel its request */
                            (RequestDealer.HelpState.STATE_OPEN == storedRequest.getState())
                            ||(RequestDealer.HelpState.STATE_POSTPONED == storedRequest.getState())
                        )&&(storedRequest.getRequesterUUID().equals(request.getUserUUID()))
                    )||(
                        ( /* In case the request is active or pending */
                            (RequestDealer.HelpState.STATE_ACTIVE == storedRequest.getState())
                            ||(RequestDealer.HelpState.STATE_PENDING == storedRequest.getState())
                        )&&( /* And one of the participants are requesting action */
                            (storedRequest.getRequesterUUID().equals(request.getUserUUID()))
                            ||(storedRequest.getHelperUUID().equals(request.getUserUUID()))
                        )
                    )
                )
            ){
                switch(storedRequest.getState()){
                    case STATE_OPEN:
                    case STATE_POSTPONED:{
                        RequestDealer.AidRequest newRequest = (
                            RequestDealer.AidRequest.newBuilder(storedRequest)
                            .setState(RequestDealer.HelpState.STATE_CANCELLED)
                            .setHelperUUID("")
                            .build()
                        );
                        requests.remove(storedRequest);
                        requests.add(newRequest);
                        responseObserver.onNext(getAcceptedResponse());
                    }break;
                    case STATE_ACTIVE:{
                        RequestDealer.AidRequest newRequest = (
                            RequestDealer.AidRequest.newBuilder(storedRequest)
                            .setState(RequestDealer.HelpState.STATE_OPEN)
                            .setHelperUUID("")
                            .build()
                        );
                        requests.remove(storedRequest);
                        requests.add(newRequest);
                        responseObserver.onNext(getAcceptedResponse());
                    }break;
                    case STATE_PENDING:{
                        RequestDealer.AidRequest newRequest = (
                            RequestDealer.AidRequest.newBuilder(storedRequest)
                            .setState(RequestDealer.HelpState.STATE_ACTIVE)
                            .build()
                        );
                        requests.remove(storedRequest);
                        requests.add(newRequest);
                        responseObserver.onNext(getAcceptedResponse());
                    }break;
                    default: responseObserver.onNext(getRejectedResponse());
                }
            }
            responseObserver.onCompleted();
        }

        @Override
        public void postpone(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(
                Level.FINE,
                "postpone request call received from : " + request.getUserUUID() + " to request + "+request.getRequestID()+"!"
            );
            RequestDealer.AidRequest storedRequest = getRequestByID(request.getRequestID());
            if( /* If the given request is found */
                (null != storedRequest)
                &&(request.getUserUUID().equals(storedRequest.getRequesterUUID())) /* The user originated the request */
                &&( /* And the request can be postponed */
                    (RequestDealer.HelpState.STATE_OPEN == storedRequest.getState())
                    ||(RequestDealer.HelpState.STATE_ACTIVE == storedRequest.getState())
                )
            ){
                RequestDealer.AidRequest newRequest = RequestDealer.AidRequest.newBuilder(storedRequest)
                .setState(RequestDealer.HelpState.STATE_POSTPONED)
                .build();
                requests.remove(storedRequest);
                requests.add(newRequest);
                responseObserver.onNext(getAcceptedResponse());
            }else{
                responseObserver.onNext(getRejectedResponse());
            }
            responseObserver.onCompleted();
        }

        @Override
        public void finalize(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.FINE,"finalize call received!");
            RequestDealer.AidRequest storedRequest = getRequestByID(request.getRequestID());
            if( /* The request is found */
                (null != storedRequest)
                &&(
                    ( /* If the state is active, both the requester and helper can finalize */
                        (RequestDealer.HelpState.STATE_ACTIVE == storedRequest.getState())
                        &&(
                            (storedRequest.getRequesterUUID().equals(request.getUserUUID()))
                            ||(storedRequest.getHelperUUID().equals(request.getUserUUID()))
                        )
                    )||( /* If the request is pending */
                        (RequestDealer.HelpState.STATE_PENDING == storedRequest.getState())
                        &&( /* Depending on who initiated the request */
                            ( /* In case the requester finalized already, the helper can finish it */
                                (storedRequest.getFinalizedBy().equals(storedRequest.getRequesterUUID()))
                                &&(storedRequest.getHelperUUID().equals(request.getUserUUID()))
                            )||( /* In case the helper finalized it, the requester can finish it */
                                (storedRequest.getFinalizedBy().equals(storedRequest.getHelperUUID()))
                                &&(storedRequest.getRequesterUUID().equals(request.getUserUUID()))
                            )
                        )
                    )
                )
            ){
                switch (storedRequest.getState()){
                    case STATE_ACTIVE:{
                        RequestDealer.AidRequest newRequest = (
                            RequestDealer.AidRequest.newBuilder(storedRequest)
                            .setState(RequestDealer.HelpState.STATE_PENDING)
                            .setFinalizedBy(request.getUserUUID())
                            .build()
                        );
                        requests.remove(storedRequest);
                        requests.add(newRequest);
                        responseObserver.onNext(getAcceptedResponse());
                    }break;
                    case STATE_PENDING:{
                        if(!storedRequest.getFinalizedBy().equals(request.getUserUUID())){
                            RequestDealer.AidRequest newRequest = (
                                RequestDealer.AidRequest.newBuilder(storedRequest)
                                .setState(RequestDealer.HelpState.STATE_FINISHED)
                                .setFinalizedBy(request.getUserUUID())
                                .build()
                            );
                            requests.remove(storedRequest);
                            requests.add(newRequest);
                            responseObserver.onNext(getAcceptedResponse());
                        }else responseObserver.onNext(getRejectedResponse());
                    }break;
                    default: responseObserver.onNext(getRejectedResponse());
                }
            }
            responseObserver.onCompleted();
        }

        @Override
        public void ping(RequestDealer.AidToken request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            LOGGER.log(Level.FINE,"ping received from : " + request.getUserUUID() + "!");
            responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestResponse.QUERY_OK).build());
            responseObserver.onCompleted();
        }
    }

    public ListProperty<RequestDealer.AidRequest> getRequests(){
        return requests;
    }

    private RequestDealer.AidToken getRejectedResponse(){
        return RequestDealer.AidToken.newBuilder()
        .setState(RequestDealer.RequestResponse.QUERY_REJECTED)
        .build();
    }

    private RequestDealer.AidToken getAcceptedResponse(){
        return RequestDealer.AidToken.newBuilder()
        .setState(RequestDealer.RequestResponse.QUERY_OK)
        .build();
    }

    public RequestDealer.AidRequest getRequestByID(String id){
        for(RequestDealer.AidRequest request : requests)
            if(request.getRequestID().equals(id))return request;
        LOGGER.log(Level.WARNING, "Couldn't find request " + id);
        return null;
    }

    public boolean removeRequestByID(String id){
        final boolean[] removed = {false};
        requests.removeIf(request -> {
            if(request.getRequestID().equals(id)){
                removed[0] = true;
                return true;
            } return false;
        });
        LOGGER.log(Level.FINE,"Removed request " + id);
        return removed[0];
    }

}
