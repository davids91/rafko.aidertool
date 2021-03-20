package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AgentDashboard.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                return new AgentDashboardController(primaryStage);
            }
        });
        Parent root = loader.load();

        Scene scene = new Scene(root, 50, Screen.getPrimary().getBounds().getHeight() * 0.90);

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("/Style/main.css");
        scene.getStylesheets().add("/Style/agent.css");
        primaryStage.setX(Screen.getPrimary().getBounds().getWidth() - 50);
        primaryStage.setY(Screen.getPrimary().getBounds().getHeight()/2.0f);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }
}
