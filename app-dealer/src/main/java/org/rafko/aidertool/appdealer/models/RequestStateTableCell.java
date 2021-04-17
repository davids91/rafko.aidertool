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

import javafx.beans.property.ListProperty;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import org.rafko.aidertool.RequestDealer;

public class RequestStateTableCell extends TableCell<RequestDealer.AidRequest, String> {
    private static final RequestDealer.HelpState[] stateValues = {
        RequestDealer.HelpState.STATE_OPEN,
        RequestDealer.HelpState.STATE_POSTPONED,
        RequestDealer.HelpState.STATE_ACTIVE,
        RequestDealer.HelpState.STATE_PENDING,
        RequestDealer.HelpState.STATE_FINISHED,
        RequestDealer.HelpState.STATE_CANCELLED,
    };
    private final ListProperty<RequestDealer.AidRequest> requestsLists;
    ContextMenu editMenu = new ContextMenu();

    public RequestStateTableCell(ListProperty<RequestDealer.AidRequest> requestsLists_){
        requestsLists = requestsLists_;
        final MenuItem[] statesMenu = new MenuItem[stateValues.length];
        for(int i = 0; i < statesMenu.length; ++i){
            statesMenu[i] = new MenuItem(stateValues[i].name());
            final int finalI = i;
            statesMenu[i].setOnAction(event -> {
                RequestDealer.AidRequest toSwitch = getTableRow().getItem();
                requestsLists.remove(toSwitch);
                requestsLists.add(
                    RequestDealer.AidRequest.newBuilder(toSwitch)
                    .setState(stateValues[finalI])
                    .build()
                );
                commitEdit(getText());
            });
            editMenu.getItems().add(statesMenu[i]);
        }
        setOnMouseClicked(event -> startEdit());
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(item);
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if(null != getItem()) editMenu.show(this, Side.LEFT, 0, 0);
    }



    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);
    }
}
