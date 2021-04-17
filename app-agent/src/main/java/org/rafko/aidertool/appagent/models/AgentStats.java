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
