package org.rafko.aidertool.shared.services;

import javafx.beans.property.ListProperty;
import org.rafko.aidertool.RequestDealer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    private static final Logger LOGGER = Logger.getLogger(LogUtil.class.getName());
    public static final File tagsFile = new File("tags.prt");
    public static void readTags(ListProperty<String> tagsContainer){
        try { /* Read in tags locally if possible */
            if(tagsFile.exists()||tagsFile.createNewFile()){
                RequestDealer.AidToken token = RequestDealer.AidToken.parseFrom(new FileInputStream(tagsFile));
                tagsContainer.clear();
                tagsContainer.addAll(token.getTagsList());
                System.out.print("Tags:");
                for(String tag : tagsContainer)System.out.print(tag + ",");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to parse stored tags file" + tagsFile.getAbsolutePath(), e);
        }
    }

    public static void writeTags(ListProperty<String> tagsContainer){
        System.out.println("Writing tags yout!");
        try { /* Read in tags locally if possible */
            if(tagsFile.exists()||tagsFile.createNewFile()){
                RequestDealer.AidToken token = RequestDealer.AidToken.newBuilder()
                    .addAllTags(tagsContainer)
                    .build();
                token.writeTo(new FileOutputStream(tagsFile));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to write stored tags file " + tagsFile.getAbsolutePath(), e);
        }
    }
}
