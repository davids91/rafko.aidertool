package Services;

import org.rafko.AiderTool.RequestDealer;
import org.rafko.AiderTool.RequestHandlerGrpc;
import io.grpc.Channel;

public class RequesterClient {
    private final RequestHandlerGrpc.RequestHandlerBlockingStub blockingCaller;
    private final String userID;
    public RequesterClient(Channel channel, String userID_){
        blockingCaller = RequestHandlerGrpc.newBlockingStub(channel);
        userID = userID_;
    }

    public void test(){
        RequestDealer.AidRequest aidRq = RequestDealer.AidRequest.newBuilder()
                .setRequesterUUID(userID)
                .build();
        /* TODO: Handle Timeout */
        if(RequestDealer.RequestState.STATE_REQUEST_OK ==  blockingCaller.ping(aidRq).getState()){
            System.out.println("Ping response received!");
        }else System.out.println("Ping response failed");
    }
}
