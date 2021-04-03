package org.rafko.aidertool.appagent.models;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.appagent.services.RequesterClient;

public class AidRequestMenuButton extends SplitMenuButton {
    public AidRequestMenuButton(AgentStats stats, RequestDealer.AidRequest request, RequesterClient client_){
        /* Contents */
        final StringBuilder stringBuilder = new StringBuilder();
        for(String tag : request.getTagsList())
            stringBuilder.append("#").append(tag).append(" ");
        setText(stringBuilder.toString());

        /* Available actions *//* TODO: realize actions */
        MenuItem initiate = new MenuItem(); /* Accept? Finalize? Prioritize(de-snooze)? */
        MenuItem finalize = new MenuItem(); /* Help request finished */
        MenuItem cancel = new MenuItem(); /* Remove the request? Take back help? */
        MenuItem snooze = new MenuItem("Snooze");
        MenuItem ignore = new MenuItem("Ignore");
        initiate.setOnAction(event -> client_.initiateRequest(request.getRequestID()));
        cancel.setOnAction(event -> client_.cancelRequest(request.getRequestID()));
        if(request.getRequesterUUID().equals(stats.getUserName())){
            snooze.setOnAction(event -> client_.postponeRequest(request.getRequestID()));
            ignore.setVisible(false);
            switch (request.getState()){
                case STATE_OPEN:{
                    initiate.setVisible(false);
                    finalize.setVisible(false);
                    cancel.setText("Delete my request");
                }break;
                case STATE_POSTPONED:{
                    finalize.setVisible(false);
                    initiate.setText("Resume..");
                    cancel.setText("Delete my request");
                }break;
                case STATE_ACTIVE:{
                    finalize.setText("I received the needed help");
                    initiate.setVisible(false);
                    cancel.setText("I can't focus on this request right now");
                }break;
                case STATE_PENDING:{
                    if(request.getFinalizedBy().equals(stats.getUserName())){
                        finalize.setText("I received the needed help");
                    }else finalize.setVisible(false);
                    initiate.setText("I accept the help I've been provided");
                    cancel.setText("I still need help..");
                }break;
                default: initiate.setVisible(false);
            }
        }else{
            snooze.setVisible(false); /* TODO: Implement "help will eventually be available" state */
            switch (request.getState()){
                case STATE_OPEN:{
                    initiate.setText("I'd like to help!");
                    ignore.setOnAction(event -> setVisible(false));
                }break;
                case STATE_POSTPONED:{
                    initiate.setVisible(false);
                    ignore.setOnAction(event -> setVisible(false));
                }break; /* TODO: implement postponed aid request response */
                case STATE_ACTIVE:{
                    if(request.getHelperUUID().equals(stats.getUserName())){
                        initiate.setText("I finished helping " + request.getRequesterUUID());
                        cancel.setText("I can no longer help " + request.getRequesterUUID() + ":(");
                        ignore.setVisible(false);
                    }else{
                        initiate.setVisible(false);
                        cancel.setVisible(false);
                    }
                }break;
                case STATE_PENDING:{
                    initiate.setVisible(false);
                    if(request.getHelperUUID().equals(stats.getUserName())){
                        if(request.getFinalizedBy().equals(stats.getUserName())){
                            cancel.setText("I am not done helping " + request.getRequesterUUID());
                            finalize.setText("I agree with " + request.getRequesterUUID() + ", help have been provided!");
                        }else{
                            cancel.setVisible(false);
                            finalize.setVisible(false);
                        }
                    }else{
                        cancel.setVisible(false);
                        initiate.setVisible(false);
                        finalize.setVisible(false);
                    }
                }break;
                default: initiate.setVisible(false);
            }
        }

        if(request.getRequesterUUID().equals(stats.getUserName())){ /* If the owner of the request is the user */
            switch (request.getState()){
                case STATE_OPEN:{getItems().addAll(snooze,cancel);}break;
                case STATE_POSTPONED:
                case STATE_ACTIVE:{getItems().addAll(initiate,cancel);}break;
                case STATE_PENDING:{getItems().addAll(cancel);}break;
            }
        }else{
            if(request.getState() == RequestDealer.HelpState.STATE_OPEN){
                getItems().add(initiate);
            }
            getItems().add(ignore);
        }

        /* User data and styling */
        switch (request.getState()){
            case STATE_OPEN:setStyle("-fx-mark-color: green;"); break;
            case STATE_POSTPONED:setStyle("-fx-mark-color: gainsboro;"); break;
            case STATE_ACTIVE:setStyle("-fx-mark-color: orange;"); break;
            case STATE_PENDING:setStyle("-fx-mark-color: yellow;"); break;
            case STATE_FINISHED:setStyle("-fx-mark-color: lightgreen;"); break;
            default: setStyle("-fx-mark-color: red;");
        }
        setUserData(request);
        setContentDisplay(ContentDisplay.RIGHT);
        setPopupSide(Side.LEFT);
        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        setPrefWidth(USE_COMPUTED_SIZE);
        setOnMouseEntered(event -> setPadding(new Insets(0, 13, 0, 2)));
        setOnMouseExited(event -> setPadding(new Insets(0, 0, 0, 0)));
    }
}
