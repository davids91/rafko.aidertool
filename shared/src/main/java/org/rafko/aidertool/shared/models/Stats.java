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
