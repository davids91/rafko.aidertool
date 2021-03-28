package org.rafko.aidertool.appagent.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rafko.aidertool.appagent.models.Stats;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(AgentApp.class.getName());
    private static final Stats stats = new Stats();

    public static void main(String[] args){
        if(0 == args.length) stats.setDealerAddress("localhost:50051");
        else if(1 >= args.length) stats.setDealerAddress(args[0] + ":50051");
        else stats.setDealerAddress(args[0] + ":" + args[1]);
        LOGGER.log(Level.INFO,"Trying dealer address: " + stats.getDealerAddress());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AgentDashboard.fxml"));
        loader.setControllerFactory(param -> new AgentDashboardController(primaryStage, stats));
        Parent root = loader.load();

        Scene scene = new Scene(root, primaryStage.getWidth(), 300);

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("/Style/main.css");
        scene.getStylesheets().add("/Style/agent.css");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }
}
