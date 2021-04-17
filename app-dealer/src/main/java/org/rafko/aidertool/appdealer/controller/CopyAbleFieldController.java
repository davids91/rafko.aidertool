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

package org.rafko.aidertool.appdealer.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyAbleFieldController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CopyAbleFieldController.class.getName());
    @FXML Label fieldNameLabel;
    @FXML ImageView clipboard_img;
    @FXML TextField fieldValueTextField;

    private final String fieldName;
    private final String fieldValue;
    public CopyAbleFieldController(String name, String value){
        fieldName = name;
        fieldValue = value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fieldNameLabel.setText(fieldName + ":");
        fieldValueTextField.setText(fieldValue);
        fieldValueTextField.setOnMouseClicked(event -> {
            /* Add the selected field to the clipboard */
            fieldValueTextField.selectAll();
            final ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, fieldValueTextField.getText());
            Clipboard.getSystemClipboard().setContent(content);
            LOGGER.log(Level.INFO, fieldName + ":" + fieldValue + "<-- on clipboard");

            /* Display the clipboard animation */
            KeyValue transparent = new KeyValue(clipboard_img.opacityProperty(), 0.0);
            KeyValue opaque = new KeyValue(clipboard_img.opacityProperty(), 1.0);
            final KeyFrame delayHideKf = new KeyFrame(Duration.ZERO, opaque);
            final KeyFrame hideKf = new KeyFrame(Duration.millis(500), transparent);
            Platform.runLater(new Timeline(delayHideKf, hideKf)::play);
        });
    }
}
