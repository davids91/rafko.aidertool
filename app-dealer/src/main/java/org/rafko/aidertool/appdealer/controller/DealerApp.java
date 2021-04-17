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

package org.rafko.aidertool.appdealer.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.rafko.aidertool.appdealer.models.DealerStats;
import org.rafko.aidertool.shared.services.LogUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DealerApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(DealerApp.class.getName());
    private static final DealerStats DEALER_STATS = new DealerStats();

    public static void main(String[] args){
        if(0 < args.length){
            LOGGER.log(Level.INFO, "Program arguments: ");
            for(String arg : args) LOGGER.log(Level.INFO, "->" + arg);
        }
        LogUtil.readTags(DEALER_STATS.getTagsProperty());
        DEALER_STATS.getTagsProperty().addListener((obs, old, newVal)
            -> LogUtil.writeTags(DEALER_STATS.getTagsProperty()));
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
                    DealerStats.ContentDefinition.getName(copyableFieldsLoaded[0]),
                    DEALER_STATS.getContent(copyableFieldsLoaded[0])
                );
            }else return new DealerDashboardController(DEALER_STATS);
        });
        Parent root = loader.load();
        Scene scene = new Scene(root, 800,200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
