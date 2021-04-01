package org.rafko.aidertool.shared.models;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;

public class Stats {
    private final ListProperty<String> tagsProperty
        = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public void setTags(String... tags_){
        tagsProperty.clear();
        addTags(tags_);
    }
    public void addTags(String... tags_){
        tagsProperty.addAll(Arrays.asList(tags_));
    }
    public void clearTags(){
        tagsProperty.clear();
    }
    public ListProperty<String> getTagsProperty(){
        return tagsProperty;
    }
}
