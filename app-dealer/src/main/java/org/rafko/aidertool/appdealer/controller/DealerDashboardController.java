package org.rafko.aidertool.appdealer.controller;

import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rafko.aidertool.RequestDealer;
import org.rafko.aidertool.appdealer.models.DealerStats;
import org.rafko.aidertool.appdealer.services.DealerServer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DealerDashboardController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(DealerDashboardController.class.getName());
    @FXML TableView<RequestDealer.AidRequest> requestsTable;
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
        knownTagsList.itemsProperty().bind(stats.getTagsProperty());
        requestsTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            stateColumn.setPrefWidth(newValue.doubleValue()/3.0);
            requesterColumn.setPrefWidth(newValue.doubleValue()/3.0);
            tagsColumn.setPrefWidth(newValue.doubleValue()/2.0);
        });

        try { dealerServer.start(50051); }
        catch (IOException e) { LOGGER.log(Level.SEVERE, "Unable to start server!", e); }

        /* Set factories for the cells */
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requesterUUID"));
        /* TODO: Store requests */
    }
}
