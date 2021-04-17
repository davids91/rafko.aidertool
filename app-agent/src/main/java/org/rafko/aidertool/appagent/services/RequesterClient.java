/*! This file is part of davids91/rafko.aidertool.
 *
 *    Rafko is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Rafko is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Rafko.  If not, see <https://www.gnu.org/licenses/> or
 *    <https://github.com/davids91/rafko.aidertool/blob/main/LICENSE>
 */

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
        RequestDealer.AidToken token = RequestDealer.AidToken.newBuilder()
        .setUserUUID(userID)
        .build();
        try{
            if(RequestDealer.RequestResponse.QUERY_OK ==  blockingCaller.ping(token).getState())
                return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Unable to communicate with channel!", e);
        }
        return false;
    }

    public RequestDealer.AidToken addRequest(RequestDealer.AidRequest request){
        return blockingCaller.add(request);
    }

    public void updateTags(ListProperty<String> currentTags){
        RequestDealer.AidToken response = blockingCaller.queryTags(RequestDealer.AidToken.newBuilder().build());
        if(0 < response.getDataCount()) for(String tag : response.getData(0).getTagsList()){
            if(!currentTags.contains(tag))
                currentTags.add(tag);
        }
    }

    public void getRequests(ListProperty<RequestDealer.AidRequest> storedRequests){
        ArrayList<RequestDealer.AidRequest> streamedRequests = new ArrayList<>();
        Iterator<RequestDealer.AidRequest> iterator = blockingCaller.queryRequests(RequestDealer.AidToken.newBuilder().build());
        while(iterator.hasNext())
            streamedRequests.add(RequestDealer.AidRequest.newBuilder(iterator.next()).build());
        ArrayList<RequestDealer.AidRequest> requestsToRemove = new ArrayList<>();
        for(RequestDealer.AidRequest request : storedRequests){ /* Remove requests not on the other side */
            if(!streamedRequests.contains(request)){ /* Mark every request not contained in the new list to be removed */
                requestsToRemove.add(request);
            }
        }
        for(RequestDealer.AidRequest request : requestsToRemove)
            storedRequests.remove(request); /* Remove marked requests */
        for(RequestDealer.AidRequest request : streamedRequests){ /* Query Actual requests */
            if(!storedRequests.contains(request)){ /* Add the new requests to the locally stored list */
                storedRequests.add(request);
            }
        }
    }

    public boolean initiateRequest(String requestID){
        RequestDealer.AidToken response = blockingCaller.initiate(
            RequestDealer.AidToken.newBuilder()
            .setUserUUID(userID).setRequestID(requestID)
            .build()
        );
        return (response.getState() == RequestDealer.RequestResponse.QUERY_OK);
    }

    public boolean cancelRequest(String requestID){
        RequestDealer.AidToken response = blockingCaller.cancel(
            RequestDealer.AidToken.newBuilder()
            .setUserUUID(userID).setRequestID(requestID)
            .build()
        );
        return (response.getState() == RequestDealer.RequestResponse.QUERY_OK);
    }

    public boolean postponeRequest(String requestID){
        RequestDealer.AidToken response = blockingCaller.postpone(
            RequestDealer.AidToken.newBuilder()
            .setUserUUID(userID).setRequestID(requestID)
            .build()
        );
        return (response.getState() == RequestDealer.RequestResponse.QUERY_OK);
    }

    public boolean finalizeRequest(String requestID){
        RequestDealer.AidToken response = blockingCaller.finalize(
            RequestDealer.AidToken.newBuilder()
            .setUserUUID(userID).setRequestID(requestID)
            .build()
        );
        return (response.getState() == RequestDealer.RequestResponse.QUERY_OK);
    }
}
