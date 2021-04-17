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
    public static final File tagsFile = new File("tags.protobin");
    public static void readTags(ListProperty<String> tagsContainer){
        try { /* Read in tags locally if possible */
            if(tagsFile.exists()||tagsFile.createNewFile()){
                RequestDealer.DataEntry token = RequestDealer.DataEntry.parseFrom(new FileInputStream(tagsFile));
                tagsContainer.clear();
                tagsContainer.addAll(token.getTagsList());
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to parse stored tags file" + tagsFile.getAbsolutePath(), e);
        }
    }

    public static void writeTags(ListProperty<String> tagsContainer){
        try { /* Read in tags locally if possible */
            if(tagsFile.exists()||tagsFile.createNewFile()){
                RequestDealer.DataEntry token = RequestDealer.DataEntry.newBuilder()
                    .addAllTags(tagsContainer)
                    .build();
                token.writeTo(new FileOutputStream(tagsFile));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to write stored tags file " + tagsFile.getAbsolutePath(), e);
        }
    }
}
