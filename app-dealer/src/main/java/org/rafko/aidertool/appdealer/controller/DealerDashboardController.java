package org.rafko.aidertool.appdealer.controller;

import org.rafko.aidertool.appdealer.services.DealerServer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DealerDashboardController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(DealerDashboardController.class.getName());
    @FXML Accordion requestList;

    DealerServer dealerServer = new DealerServer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            dealerServer.start(50051);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "UNable to start server!", e);
        }
    }
}
