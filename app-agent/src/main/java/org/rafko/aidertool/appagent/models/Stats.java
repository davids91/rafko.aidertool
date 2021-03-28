package org.rafko.aidertool.appagent.models;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Stats {
    private static final Logger LOGGER = Logger.getLogger(Stats.class.getName());
    private final String userName = userName();
    private String dealerAddress = "localhost:50051";
    private ArrayList<String> tags = new ArrayList<>();

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

    public void setTags(String... tags_){
        tags.clear();
        addTags(tags_);
    }

    public void addTags(String... tags_){
        tags.addAll(Arrays.asList(tags_));
    }

    public ArrayList<String> getTags(){
        return tags;
    }
}
