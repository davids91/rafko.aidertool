package org.rafko.aidertool.appdealer.controller;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.rafko.AiderTool.RequestDealer;
import org.rafko.aidertool.appdealer.services.DealerServer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;

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
    @FXML Accordion requestList;
    DealerServer dealerServer = new DealerServer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requestsTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            stateColumn.setPrefWidth(newValue.doubleValue()/3.0);
            requesterColumn.setPrefWidth(newValue.doubleValue()/3.0);
            tagsColumn.setPrefWidth(newValue.doubleValue()/2.0);
        });

        try { /* try to start the server with the given parameters */
            dealerServer.start(50051);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to start server!", e);
       }

        /* Set factories fof the cells */
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requesterUUID"));
        /* TODO: Also add tags.. */

        RequestDealer.AidRequest testItem = RequestDealer.AidRequest.newBuilder()
                .setRequesterUUID("JOHN DOUGH")
                .setState(RequestDealer.RequestState.STATE_ACTIVE)
                .addAllTags(Arrays.asList("tag1", "tag2", "tag3"))
                .build();
        requestsTable.getItems().add(testItem);
    }
}
