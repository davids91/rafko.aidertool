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

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.appdealer.models.DealerStats;
import org.rafko.aidertool.appdealer.models.RequestStateTableCell;
import org.rafko.aidertool.appdealer.services.DealerServer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.rafko.aidertool.shared.services.StringUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DealerDashboardController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(DealerDashboardController.class.getName());
    @FXML TableView<RequestDealer.AidRequest> requestsTable;
    @FXML TableColumn<RequestDealer.AidRequest, String> helperColumn;
    @FXML TableColumn<RequestDealer.AidRequest, String> finalisedByColumn;
    @FXML TableColumn<RequestDealer.AidRequest, String> stateColumn;
    @FXML TableColumn<RequestDealer.AidRequest, String> requesterColumn;
    @FXML TableColumn<RequestDealer.AidRequest, String> tagsColumn;
    @FXML ListView<String> knownTagsList;
    private final DealerStats stats;
    private final DealerServer dealerServer;

    public DealerDashboardController(DealerStats stats_){
        stats = stats_;
        dealerServer = new DealerServer(stats);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Initialize Tags Lists */
        knownTagsList.itemsProperty().bind(stats.getTagsProperty());

        /* Initialize Requests table */
        requestsTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            stateColumn.setPrefWidth(newValue.doubleValue()/3.0);
            requesterColumn.setPrefWidth(newValue.doubleValue()/3.0);
            helperColumn.setPrefWidth(newValue.doubleValue()/3.0);
            finalisedByColumn.setPrefWidth(newValue.doubleValue()/3.0);
            tagsColumn.setPrefWidth(newValue.doubleValue()/2.0);
        });
        stateColumn.setCellValueFactory(param -> new Text(param.getValue().getState().toString()).textProperty());
        requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requesterUUID"));
        helperColumn.setCellValueFactory(new PropertyValueFactory<>("helperUUID"));
        finalisedByColumn.setCellValueFactory(new PropertyValueFactory<>("finalizedBy"));
        tagsColumn.setCellValueFactory(param -> {
            StringBuilder tagsString = new StringBuilder();
            if(0 < param.getValue().getDataCount()) for(String tag : param.getValue().getData(0).getTagsList()){
                tagsString.append(tag).append(", ");
            }
            return new Text(StringUtil.replaceLast(tagsString.toString(),", *", "")).textProperty();
        });
        requestsTable.itemsProperty().bind(dealerServer.getRequests());

        /* Context menu for Requests */
        stateColumn.setCellFactory(tableColumn -> new RequestStateTableCell(dealerServer.getRequests()));

        /* Try to start the Dealer Server */
        try { dealerServer.start(50051); }
        catch (IOException e) { LOGGER.log(Level.SEVERE, "Unable to start server!", e); }
    }
}
