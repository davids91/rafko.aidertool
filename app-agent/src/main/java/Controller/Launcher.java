package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class Launcher extends Application {

    public static void main(String[] args){
        for(String arg : args) System.out.println(arg);
        launch(args);
    }

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

        Scene scene = new Scene(root, primaryStage.getWidth(), 200);

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("/Style/main.css");
        scene.getStylesheets().add("/Style/agent.css");
        primaryStage.setX(Screen.getPrimary().getBounds().getWidth() - primaryStage.getWidth());
        primaryStage.setY(Screen.getPrimary().getBounds().getHeight()/2.0f);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }
}
