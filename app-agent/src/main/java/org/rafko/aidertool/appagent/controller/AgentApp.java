package org.rafko.aidertool.appagent.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(AgentApp.class.getName());
    private static String dealerAddress;

    public static void main(String[] args){
        if(0 == args.length)
            dealerAddress = "localhost:50051";
        else if(1 >= args.length)
            dealerAddress = args[0] + ":50051";
        else dealerAddress = args[0] + ":" + args[1];
        LOGGER.log(Level.INFO,"Trying dealer address: " + dealerAddress);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AgentDashboard.fxml"));
        loader.setControllerFactory(param -> new AgentDashboardController(primaryStage, dealerAddress));
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
