package org.rafko.aidertool.appagent.models;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import org.rafko.aidertool.RequestDealer;

public class AidRequestMenuButton extends SplitMenuButton {
    private final MenuItem ignore;
    private final MenuItem acceptReject;
    private final MenuItem snooze;
    private final MenuItem finalize;
    private final MenuItem cancel;

    public AidRequestMenuButton(RequestDealer.AidRequest request){
        /* Contents */
        final StringBuilder stringBuilder = new StringBuilder();
        for(String tag : request.getTagsList())
            stringBuilder.append("#").append(tag).append(" ");
        setText(stringBuilder.toString());

        /* Available actions *//* TODO: realize actions */
        ignore = new MenuItem("Ignore");
        acceptReject = new MenuItem("Accept");
        acceptReject.setVisible(false);
        snooze = new MenuItem("Snooze");
        finalize = new MenuItem("Finalize");
        cancel = new MenuItem("Cancel");
        getItems().addAll(ignore,acceptReject,snooze,finalize,cancel);

        /* Userdata and styling */
        switch (request.getState()){
            case STATE_OPEN:setStyle("-fx-mark-color: green;"); break;
            case STATE_POSTPONED:setStyle("-fx-mark-color: cadetblue;"); break;
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
        setStyle("hoverable");
        setOnMouseEntered(event -> setPadding(new Insets(0, 13, 0, 2)));
        setOnMouseExited(event -> setPadding(new Insets(0, 0, 0, 0)));
    }
}
