/*! This file is part of davids91/rafko.aidertool.
 *
 *    Rafko is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Rafko is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Rafko.  If not, see <https://www.gnu.org/licenses/> or
 *    <https://github.com/davids91/rafko.aidertool/blob/main/LICENSE>
 */

package org.rafko.aidertool.appagent.controller;
import org.rafko.aidertool.appagent.models.AgentStats;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rafko.aidertool.shared.services.LogUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(AgentApp.class.getName());
    private static final AgentStats AGENT_STATS = new AgentStats();

    public static void main(String[] args){
        if(0 < args.length){
            LOGGER.log(Level.INFO, "Program arguments: ");
            for(String arg : args) LOGGER.log(Level.INFO, "->" + arg);
        }
        if(0 == args.length){
            AGENT_STATS.setDealerAddress("localhost:50051");
            /* try to read in INI file, create it if doesn't exist */
        }else if(1 >= args.length) AGENT_STATS.setDealerAddress(args[0] + ":50051");
        else AGENT_STATS.setDealerAddress(args[0] + ":" + args[1]);
        LOGGER.log(Level.INFO,"Trying dealer address: " + AGENT_STATS.getDealerAddress());
        LogUtil.readTags(AGENT_STATS.getTagsProperty());
        AGENT_STATS.getTagsProperty().addListener((obs, old, newVal)
            -> LogUtil.writeTags(AGENT_STATS.getTagsProperty()));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AgentDashboard.fxml"));
        loader.setControllerFactory(param -> new AgentDashboardController(primaryStage, AGENT_STATS));
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
