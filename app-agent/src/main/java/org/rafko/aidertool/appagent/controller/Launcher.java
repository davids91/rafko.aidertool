package org.rafko.aidertool.appagent.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {
    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
    public static void main(String[] args){
        if(0 < args.length){
            LOGGER.log(Level.INFO, "Program arguments: ");
            for(String arg : args) LOGGER.log(Level.INFO, "->" + arg);
        }
        AgentApp.main(args);
    }
}
