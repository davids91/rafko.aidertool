package Services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.rafko.AiderTool.RequestDealer;
import org.rafko.AiderTool.RequestHandlerGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DealerServer {
    private static final Logger logger = Logger.getLogger(DealerServer.class.getName());
    private Server server;

    public void start(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RequestHandlerImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    DealerServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
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
            System.out.println("addRequest call received from : " + request.getRequesterUUID() + "!");
        }

        @Override
        public void cancelRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            System.out.println("addRequest call received!");
        }

        @Override
        public void queryRequest(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            System.out.println("cancelRequest call received!");
        }

        @Override
        public void queryRequests(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            System.out.println("queryRequests call received!");
        }

        @Override
        public void initiate(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            System.out.println("initiate call received!");
        }

        @Override
        public void finalize(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {
            System.out.println("finalize call received!");
        }

        @Override
        public void ping(RequestDealer.AidRequest request, StreamObserver<RequestDealer.AidToken> responseObserver) {
            System.out.println("ping received from : " + request.getRequesterUUID() + "!");
            responseObserver.onNext(RequestDealer.AidToken.newBuilder().setState(RequestDealer.RequestState.STATE_REQUEST_OK).build());
            responseObserver.onCompleted();
        }
    }

}
