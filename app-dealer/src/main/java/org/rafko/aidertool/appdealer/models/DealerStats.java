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

package org.rafko.aidertool.appdealer.models;

import org.rafko.aidertool.shared.services.NetUtil;
import org.rafko.aidertool.shared.models.Stats;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DealerStats extends Stats {
    private static final Logger LOGGER = Logger.getLogger(DealerStats.class.getName());
    public enum ContentDefinition{
        LAN_IP, WAN_IP, CONTENTS_SIZE;
        private static final ContentDefinition[] valuesArr = values();
        public static ContentDefinition get(int index){ return valuesArr[index];}
        public static String getName(int index){return get(index).name();}
    };
    private final String[] contents;

    public DealerStats() {
        contents = new String[ContentDefinition.CONTENTS_SIZE.ordinal()];
        contents[ContentDefinition.LAN_IP.ordinal()] = NetUtil.getLANIP();
        contents[ContentDefinition.WAN_IP.ordinal()] = NetUtil.getWANIP();
    }

    public String getContent(int index){
        if((0 <= index)&&(ContentDefinition.CONTENTS_SIZE.ordinal() > index))
            return contents[index];
        else{
            LOGGER.log(Level.SEVERE, "Content index("+index+"/"+ContentDefinition.CONTENTS_SIZE.ordinal()+") out of bounds!");
            return "";
        }
    }
}
