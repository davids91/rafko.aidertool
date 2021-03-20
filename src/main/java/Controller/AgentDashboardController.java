package Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AgentDashboardController {

    @FXML
    private Button moveButton;
    @FXML
    private GridPane rootGrid;

    private final Stage primaryStage;
    private double xOffset = 0;
    private double yOffset = 0;

    public AgentDashboardController(Stage parent_){
        primaryStage = parent_;
    }
    @FXML
    public void initialize() {
        moveButton.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        moveButton.setOnMouseDragged(event -> {
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }

    public void quitApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
