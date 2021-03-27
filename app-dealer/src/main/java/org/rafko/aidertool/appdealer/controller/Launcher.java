package org.rafko.aidertool.appdealer.controller;

import org.rafko.aidertool.appdealer.models.Stats;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher extends Application {
    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
    private static final Stats stats = new Stats();

    public static void main(String[] args){
        if(0 < args.length){
            LOGGER.log(Level.INFO, "Program arguments: ");
            for(String arg : args) LOGGER.log(Level.INFO, "->" + arg);
        }
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
        Parent root = loader.load();
        Scene scene = new Scene(root, 800,200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
