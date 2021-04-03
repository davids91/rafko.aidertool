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
        if(request.getRequesterUUID().equals(stats.getUserName())){
            MenuItem acceptReject = new MenuItem("Accept");
            acceptReject.setVisible(false);
            MenuItem cancel = new MenuItem("Cancel");
            cancel.setOnAction(event -> client_.cancelRequest(request.getRequestID()));
            getItems().addAll(acceptReject,cancel);
        }
        MenuItem ignore = new MenuItem("Ignore");
        MenuItem snooze = new MenuItem("Snooze");
        MenuItem finalize = new MenuItem("Finalize");
        getItems().addAll(ignore,snooze,finalize);

        /* Userdata and styling */
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
