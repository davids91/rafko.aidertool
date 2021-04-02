package org.rafko.aidertool.appagent.services;

import javafx.beans.property.ListProperty;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.RequestHandlerGrpc;
import io.grpc.Channel;

import java.util.ArrayList;
import java.util.Iterator;
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

    public boolean testConnection(){
        RequestDealer.AidRequest aidRq = RequestDealer.AidRequest.newBuilder()
                .setRequesterUUID(userID)
                .build();
        try{
            if(RequestDealer.RequestState.STATE_REQUEST_OK ==  blockingCaller.ping(aidRq).getState())
                return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Unable to communicate with channel!", e);
        }
        return false;
    }

    public RequestDealer.AidToken addRequest(RequestDealer.AidRequest request){
        return blockingCaller.addRequest(request);
    }

    public void updateTags(ListProperty<String> currentTags){
        RequestDealer.AidToken response = blockingCaller.queryTags(RequestDealer.AidToken.newBuilder().build());
        for(String tag : response.getTagsList()){
            if(!currentTags.contains(tag))
                currentTags.add(tag);
        }
    }

    public ArrayList<RequestDealer.AidRequest> getRequests(){
        ArrayList<RequestDealer.AidRequest> requests = new ArrayList<>();
        Iterator<RequestDealer.AidRequest> iterator = blockingCaller.queryRequests(RequestDealer.AidToken.newBuilder().build());
        while(iterator.hasNext())
            requests.add(RequestDealer.AidRequest.newBuilder(iterator.next()).build());
        return requests;
    }
}
