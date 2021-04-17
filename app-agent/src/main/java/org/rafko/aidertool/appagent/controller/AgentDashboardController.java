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

package org.rafko.aidertool.appagent.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.appagent.models.AgentStats;
import org.rafko.aidertool.appagent.models.AidRequestMenuButton;
import org.rafko.aidertool.appagent.services.RequesterClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AgentDashboardController.class.getName());
    private static final Image notConnectedIcon = new Image("Img/not_connected.png");
    private static final Image connectedIcon = new Image("Img/connected.png");
    private static final int MS_WIGGLE_ROOM = 3;
    private static final int CONNECTION_RECHECK_AFTER_MS = 1000;
    private static final int REQUESTS_RECHECK_AFTER_MS = 5000;
    private static final int TAGS_RECHECK_AFTER_MS = 100000;
    @FXML AnchorPane rootPanel;
    @FXML MenuItem requestHelpBtn;
    @FXML ImageView statusIcon;
    @FXML HBox statusBar;
    @FXML MenuButton menuButton;
    @FXML Button moveButton;
    @FXML VBox rootVBox;
    @FXML Label userId;

    private final Stage primaryStage;
    private final Thread connectionThread;
    private final Thread syncThread;
    private final RequesterClient caller;
    private final AgentStats agentStats;
    private final ListProperty<RequestDealer.AidRequest> requests;
    private double yOffset = 0;
    private boolean hideStage = false;
    private boolean running = true;
    private boolean connected = false;
    private boolean tagsChecked = false;
    private boolean requestsChecked = false;

    public AgentDashboardController(Stage parent_, AgentStats agentStats_){
        agentStats = agentStats_;
        primaryStage = parent_;
        requests = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        ManagedChannel channel = ManagedChannelBuilder.forTarget(agentStats.getDealerAddress())
                .usePlaintext().build(); /* TODO: use SSL/TLS */
        caller = new RequesterClient(channel, agentStats.getUserName());
        connectionThread = new Thread(this::checkConnection);
        syncThread = new Thread(this::sync);
    }

    public void sendHelpRequest(List<String> tags){
        if(isConnected()){ /* TODO: Handle communication in an async way */
            RequestDealer.AidRequest request = RequestDealer.AidRequest.newBuilder()
                .setRequesterUUID(agentStats.getUserName())
                .addData(RequestDealer.DataEntry.newBuilder()
                    .addAllTags(tags)
                    .build())
                .build();
            if(RequestDealer.RequestResponse.QUERY_OK == caller.addRequest(request).getState())
                LOGGER.log(Level.FINE, "Help Request successfully sent");
            requestDataOffbeat();
        }else LOGGER.log(Level.SEVERE,"Unable to request help, no recipient found..");
    }

    @FXML
    public void initialize(){
        /* UI related initialization */
        userId.setText(agentStats.getUserName());
        moveButton.setOnMousePressed(event -> yOffset = event.getSceneY());
        moveButton.setOnMouseDragged(event -> primaryStage.setY(event.getScreenY() - yOffset));

        /* Set queried requests list changeListener */
        requests.addListener((ListChangeListener<? super RequestDealer.AidRequest>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for(RequestDealer.AidRequest request : change.getAddedSubList()){
                        Platform.runLater(()->rootVBox.getChildren().add(new AidRequestMenuButton(agentStats, request, caller)));
                    }
                }else{
                    Platform.runLater(()->rootVBox.getChildren().removeIf(node ->
                        (null != node.getUserData()) && (change.getRemoved().contains((RequestDealer.AidRequest)node.getUserData()))
                    ));
                }
            }
        });

        /* Create hover functionalities */
        hideStage();
        rootPanel.addEventFilter(
            MouseEvent.MOUSE_ENTERED,
            event -> {
                hideStage = false;
                showStage();
            }
        );
        rootPanel.addEventFilter(
            MouseEvent.MOUSE_EXITED,
            event -> {
                final KeyFrame delayHideKf = new KeyFrame(Duration.ZERO, e -> hideStage = true);
                final KeyFrame hideKf = new KeyFrame(Duration.millis(500), e -> { if(hideStage) hideStage(); });
                Platform.runLater(new Timeline(delayHideKf, hideKf)::play);
            }
        );

        /* server connection */
        connectionThread.start();
        syncThread.start();

        /* contract the stage */
        hideStage();
    }

    public void requestDataOffbeat(){
        if (isConnected()) {
            caller.getRequests(requests);
            caller.updateTags(agentStats.getTagsProperty());
        }
    }

    private void trySync(){
        if (isConnected()) {
            if(
                (!tagsChecked) /* check the tags only once per a few sawtooth iterations */
                &&(MS_WIGGLE_ROOM > (System.currentTimeMillis() % TAGS_RECHECK_AFTER_MS))
            ){ /* Query tags */
                caller.updateTags(agentStats.getTagsProperty());
                tagsChecked = true;
            }
            if(MS_WIGGLE_ROOM < (System.currentTimeMillis() % TAGS_RECHECK_AFTER_MS)){
                tagsChecked = false;
            }

            if((!requestsChecked)&&(MS_WIGGLE_ROOM > (System.currentTimeMillis() % REQUESTS_RECHECK_AFTER_MS))){
                caller.getRequests(requests);
                requestsChecked = true;
            }
            if(MS_WIGGLE_ROOM < (System.currentTimeMillis() % REQUESTS_RECHECK_AFTER_MS)){
                requestsChecked = false;
            }
            /* TODO: Filter query based on tags */
        }
    }

    private void sync(){
        while(isRunning()) {
            trySync();
        }
    }

    private void checkConnection(){
        while(isRunning()){
            tryConnection();
        }
    }

    private void tryConnection(){
        if(MS_WIGGLE_ROOM > (System.currentTimeMillis() % CONNECTION_RECHECK_AFTER_MS)){
            if (caller.testConnection()) {
                setConnected(true);
                Platform.runLater(this::setUIToConnected);
            } else {
                setConnected(false);
                Platform.runLater(this::setUItoDisconnected);
            }
        }
    }

    private void setUIToConnected(){
        requestHelpBtn.setDisable(false);
        statusIcon.setImage(connectedIcon);
    }

    private void setUItoDisconnected(){
        requestHelpBtn.setDisable(true);
        statusIcon.setImage(notConnectedIcon);
    }

    WritableValue<Double> writableStageWidth = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return primaryStage.getWidth();
        }

        @Override
        public void setValue(Double value) {
            primaryStage.setWidth(value);
            primaryStage.setX(Screen.getPrimary().getBounds().getWidth()-(primaryStage.getWidth()));
        }
    };

    private void hideStage(){
        Platform.runLater(
            new Timeline(
                new KeyFrame(Duration.ZERO,event -> {
                    rootVBox.setAlignment(Pos.CENTER_LEFT);
                    statusBar.setAlignment(Pos.CENTER_RIGHT);
                    userId.setVisible(false);
                    menuButton.setPrefWidth(0.0);
                }),
                new KeyFrame(Duration.millis(300),new KeyValue(writableStageWidth, 30.0))
            )::play
        );
    }

    private void showStage(){
        Platform.runLater(new Timeline( /* TODO: Use maximum size of request buttons */
            new KeyFrame(Duration.ZERO,event -> {
                rootVBox.setAlignment(Pos.CENTER_RIGHT);
                statusBar.setAlignment(Pos.CENTER_RIGHT);
                userId.setVisible(true);
                menuButton.setPrefWidth(MenuButton.USE_COMPUTED_SIZE);
            }),
            new KeyFrame(Duration.millis(50),new KeyValue(writableStageWidth, 300.0))
        )::play);
    }

    public void quitApp() {
        stopRunning();
        try {
            connectionThread.join();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Connection checked thread interrupted",e);
        }
        Platform.exit();
        System.exit(0);
    }

    public void requestHelpDialog() {
            tryConnection();
            if(isConnected()){
                try {
                    Stage tagTest = new Stage();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/TagsEditor.fxml"));
                    loader.setControllerFactory(param -> new TagsEditorController(agentStats, this::sendHelpRequest));
                    Parent root = loader.load();
                    Scene tagsScene = new Scene(root, 400, 200);
                    tagTest.setScene(tagsScene);
                    tagTest.show();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Unable to load Tags view!", e);
                }

            }
    }

    private synchronized boolean isConnected(){
        return connected;
    }

    private synchronized boolean isRunning(){
        return running;
    }

    private synchronized void setConnected(boolean newValue){
        connected = newValue;
    }

    private synchronized void stopRunning(){
        running = false;
    }
}
