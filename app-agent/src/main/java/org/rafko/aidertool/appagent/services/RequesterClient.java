package org.rafko.aidertool.appagent.services;

import org.rafko.AiderTool.RequestDealer;
import org.rafko.AiderTool.RequestHandlerGrpc;
import io.grpc.Channel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RequesterClient {
    private static final Logger LOGGER = Logger.getLogger(RequesterClient.class.getName());
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
            LOGGER.log(Level.INFO,"Ping response received!");
        }else LOGGER.log(Level.INFO,"Ping response failed");
    }
}
