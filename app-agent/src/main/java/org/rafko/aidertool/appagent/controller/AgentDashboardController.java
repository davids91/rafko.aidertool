package org.rafko.aidertool.appagent.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
import org.rafko.aidertool.appagent.services.RequesterClient;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AgentDashboardController.class.getName());
    private static final Image notConnectedIcon = new Image("Img/not_connected.png");
    private static final Image connectedIcon = new Image("Img/connected.png");
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
    private double yOffset = 0;
    private boolean hideStage = false;
    private boolean running = true;
    private boolean connected = false;

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

    public AgentDashboardController(Stage parent_, AgentStats agentStats_){
        agentStats = agentStats_;
        primaryStage = parent_;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(agentStats.getDealerAddress())
                .usePlaintext().build(); /* TODO: use SSL/TLS */
        caller = new RequesterClient(channel, agentStats.getUserName());
        connectionThread = new Thread(this::checkConnection);
        syncThread = new Thread(this::sync);
    }

    public void sendHelpRequest(List<String> tags){
        if(isConnected()){ /* TODO: Handle communication in an async way */
            RequestDealer.AidRequest request = RequestDealer.AidRequest.newBuilder()
                    .addAllTags(tags).setRequesterUUID(agentStats.getUserName())
                    .build();
            if(RequestDealer.RequestState.STATE_REQUEST_OK ==
            caller.addRequest(request)/* TODO: process accepted / denied requests */
            .getState())System.out.println("SUCCESS!");
        }else LOGGER.log(Level.SEVERE,"Unable to request help, no recipient found..");
    }

    @FXML
    public void initialize() {
        /* UI related initialization */
        userId.setText(agentStats.getUserName());
        moveButton.setOnMousePressed(event -> yOffset = event.getSceneY());
        moveButton.setOnMouseDragged(event -> primaryStage.setY(event.getScreenY() - yOffset));

        /* Generate mockup buttons */
        for(int i = 0; i < 5; ++i){
            /* create SplitMenuButton */
            rootVBox.getChildren().add(createButtonForAidRequests("EVC", "SDH", "C", "VehISP"));
        }

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

    private void sync(){
        while(isRunning()) {
            if (isConnected()) {
                caller.updateTags(agentStats.getTagsProperty());
                /* TODO: Query requests */
                /* TODO: Filter query based on tags */
                /* TODO: Update UI based on available requests */
                pauseThread();
            }
        }
    }

    private void checkConnection(){
        while(isRunning()){
            if (caller.testConnection()) {
                setConnected(true);
                Platform.runLater(this::setUIToConnected);
            } else {
                setConnected(false);
                Platform.runLater(this::setUItoDisconnected);
            }
            pauseThread();
        }
    }

    private void pauseThread(){
        try {
            if(isConnected()) Thread.sleep(30000);
            else Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING,"Connection thread interrupted!");
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

    private SplitMenuButton createButtonForAidRequests(String... tags){
        SplitMenuButton button = new SplitMenuButton();
        final StringBuilder stringBuilder = new StringBuilder();
        for(String tag : tags){
            stringBuilder.append("#");
            stringBuilder.append(tag);
            stringBuilder.append(" ");
        }
        button.setText(stringBuilder.toString());

        button.setContentDisplay(ContentDisplay.RIGHT);
        button.setPopupSide(Side.LEFT);
        button.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        button.setMaxSize(250,30);
        button.setOnMouseEntered(event -> button.setPrefSize((9 * stringBuilder.length()),30));
        button.setOnMouseExited(event -> button.setPrefSize(0,30));
        return button;
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
