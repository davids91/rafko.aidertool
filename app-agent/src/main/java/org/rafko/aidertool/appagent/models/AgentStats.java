package org.rafko.aidertool.appagent.models;

import org.rafko.aidertool.shared.models.Stats;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AgentStats extends Stats {
    private static final Logger LOGGER = Logger.getLogger(AgentStats.class.getName());
    private final String userName = userName();
    private String dealerAddress = "localhost:50051";

    private String userName(){
        String value;
        try {
            value = java.net.InetAddress.getLocalHost().getHostName() + "/" + System.getProperty("user.name");
        } catch (UnknownHostException e) {
            value = System.getProperty("user.name");
            LOGGER.log(Level.WARNING, "Unable to reach localhost?!", e);
        }
        return value;
    }

    public String getUserName() {
        return userName;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }
}
