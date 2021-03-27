package Controller;

import Services.DealerServer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;


public class DealerDashboardController implements Initializable {

    @FXML Accordion requestList;

    DealerServer dealerServer = new DealerServer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            dealerServer.start(50051);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
