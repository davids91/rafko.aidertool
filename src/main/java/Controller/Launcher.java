package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DealerApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View/DealerDashboard.fxml"));

        Scene scene = new Scene(root, 800, 300);

        primaryStage.setTitle("Dealer Backend");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
