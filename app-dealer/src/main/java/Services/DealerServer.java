package Services;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.rafko.AiderTool.RequestDealer;
import org.rafko.AiderTool.RequestHandlerGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DealerServer {
    private static final Logger logger = Logger.getLogger(DealerServer.class.getName());
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
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

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class RequestHandlerImpl extends RequestHandlerGrpc.RequestHandlerImplBase {
        @Override
        public void addRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {

        }

        @Override
        public void cancelRequest(RequestDealer.AidRequest request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {

        }

        @Override
        public void queryRequest(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {

        }

        @Override
        public void queryRequests(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {

        }

        @Override
        public void initiate(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {

        }

        @Override
        public void finalize(RequestDealer.AidToken request, io.grpc.stub.StreamObserver<RequestDealer.AidToken> responseObserver) {

        }
    }

}
