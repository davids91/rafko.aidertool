package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class AgentDashboardController {
    private final Image not_connected_icon = new Image("Img/not_connected.png");
    private final Image connected_icon = new Image("Img/connected.png");

    @FXML
    private HBox status_bar;
    @FXML
    private MenuButton menuButton;
    @FXML
    private Button moveButton;
    @FXML
    private VBox rootVBox;

    private final Stage primaryStage;
    private double yOffset = 0;
    private boolean hideStage = false;

    public AgentDashboardController(Stage parent_){
        primaryStage = parent_;
    }
    @FXML
    public void initialize() {
        ImageView icon = new ImageView(not_connected_icon);
        icon.setFitWidth(15);
        icon.setFitHeight(15);
        menuButton.setGraphic(icon);

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
    }

    private void hideStage(){
        status_bar.setAlignment(Pos.CENTER_LEFT);
        rootVBox.setAlignment(Pos.CENTER_LEFT);
        Platform.runLater(()-> primaryStage.setX(Screen.getPrimary().getBounds().getWidth()-(primaryStage.getWidth()*0.1)));
    }

    private void showStage(){
        status_bar.setAlignment(Pos.CENTER_RIGHT);
        rootVBox.setAlignment(Pos.CENTER_RIGHT);
        Platform.runLater(()->primaryStage.setX(Screen.getPrimary().getBounds().getWidth() - primaryStage.getWidth()));
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
        Platform.exit();
        System.exit(0);
    }
}
