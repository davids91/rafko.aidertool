package org.rafko.aidertool.appagent.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.rafko.aidertool.appagent.models.Stats;
import org.rafko.aidertool.appagent.services.RequesterClient;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AgentDashboardController.class.getName());
    private final Image notConnectedIcon = new Image("Img/not_connected.png");
    private final Image connectedIcon = new Image("Img/connected.png");
    @FXML ImageView statusIcon;
    @FXML HBox statusBar;
    @FXML MenuButton menuButton;
    @FXML Button moveButton;
    @FXML VBox rootVBox;
    @FXML Label userId;

    private final Stage primaryStage;
    private double yOffset = 0;
    private boolean hideStage = false;
    private final Thread connectionThread;
    private final RequesterClient caller;
    private final Stats stats;
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

    public AgentDashboardController(Stage parent_, Stats stats_){
        stats = stats_;
        primaryStage = parent_;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(stats.getDealerAddress())
                .usePlaintext().build(); /* TODO: use SSL/TLS */
        caller = new RequesterClient(channel, stats.getUserName());
        connectionThread = new Thread(() -> {
            while(isRunning()){
                if (caller.testConnection()) {
                    setConnected(true);
                    setUIToConnected();
                } else {
                    setConnected(true);
                    setUItoDisconnected();
                }
                try {
                    if(isConnected()) Thread.sleep(30000);
                    else Thread.sleep(5000);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING,"Connection thread interrupted!");
                }
            }
        });

    }
    @FXML
    public void initialize() {
        userId.setText(stats.getUserName());
        moveButton.setOnMousePressed(event -> yOffset = event.getSceneY());
        moveButton.setOnMouseDragged(event -> primaryStage.setY(event.getScreenY() - yOffset));

        /* Generate mockup buttons */
        for(int i = 0; i < 5; ++i){
            /* create SplitMenuButton */
            rootVBox.getChildren().add(createButtonForAidRequests("EVC", "SDH", "C", "VehISP"));
        }

        /* Create hover functionalities */
        hideStage();
        rootVBox.addEventFilter(
            MouseEvent.MOUSE_ENTERED,
            event -> {
                hideStage = false;
                showStage();
            }
        );
        rootVBox.addEventFilter(
            MouseEvent.MOUSE_EXITED,
            event -> {
                final KeyFrame delayHideKf = new KeyFrame(Duration.ZERO, e -> hideStage = true);
                final KeyFrame hideKf = new KeyFrame(Duration.millis(500), e -> { if(hideStage) hideStage(); });
                Platform.runLater(new Timeline(delayHideKf, hideKf)::play);
            }
        );
        connectionThread.start();
    }

    private void setUIToConnected(){
        statusIcon.setImage(connectedIcon);
    }

    private void setUItoDisconnected(){
        statusIcon.setImage(notConnectedIcon);
    }

    private void hideStage(){
        rootVBox.setAlignment(Pos.CENTER_LEFT);
        statusBar.setAlignment(Pos.CENTER_RIGHT);
        userId.setVisible(false);
        Platform.runLater(() -> {
            menuButton.setPrefWidth(0.0);
            primaryStage.setWidth(30);
            primaryStage.setX(Screen.getPrimary().getBounds().getWidth()-(primaryStage.getWidth()));
        });
    }

    private void showStage(){
        rootVBox.setAlignment(Pos.CENTER_RIGHT);
        statusBar.setAlignment(Pos.CENTER_RIGHT);
        userId.setVisible(true);
        Platform.runLater(() -> {
            menuButton.setPrefWidth(MenuButton.USE_COMPUTED_SIZE);
            primaryStage.setWidth(300);
            primaryStage.setX(Screen.getPrimary().getBounds().getWidth()-(primaryStage.getWidth()));
        });
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
        button.setOnMouseEntered(event -> {
            Platform.runLater(new Timeline(new KeyFrame(Duration.millis(20), e -> hideStage = false))::play);
            button.setPrefSize((9 * stringBuilder.length()),30);
        });
        button.setOnMouseExited(event -> {button.setPrefSize(0,30);});
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
}
