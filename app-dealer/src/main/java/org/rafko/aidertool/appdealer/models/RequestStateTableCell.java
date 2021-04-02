package org.rafko.aidertool.appdealer.models;

import javafx.beans.property.ListProperty;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import org.rafko.aidertool.RequestDealer;

public class RequestStateTableCell extends TableCell<RequestDealer.AidRequest, String> {
    private static final RequestDealer.RequestState[] stateValues = {
        RequestDealer.RequestState.STATE_OPEN,
        RequestDealer.RequestState.STATE_POSTPONED,
        RequestDealer.RequestState.STATE_ACTIVE,
        RequestDealer.RequestState.STATE_PENDING,
        RequestDealer.RequestState.STATE_FINISHED,
        RequestDealer.RequestState.STATE_CANCELLED,
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
