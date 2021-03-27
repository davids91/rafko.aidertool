package Controller;

import Models.Stats;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    private static Stats stats = new Stats();

    public static void main(String[] args){
        for(String arg : args) System.out.println(arg);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final int[] copyableFieldsLoaded = {-1};
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/DealerDashboard.fxml"));
        loader.setControllerFactory((Class<?> controllerType) ->{
            if(controllerType == CopyAbleFieldController.class){
                ++copyableFieldsLoaded[0];
                return new CopyAbleFieldController(
                    Stats.ContentDefinition.getName(copyableFieldsLoaded[0]),
                    stats.getContent(copyableFieldsLoaded[0])
                );
            }else{
                return new DealerDashboardController();
            }
        });
        Parent root = loader.load();        Scene scene = new Scene(root, 800,200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
