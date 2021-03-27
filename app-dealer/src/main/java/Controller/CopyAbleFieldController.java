package Controller;

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

public class CopyAbleFieldController implements Initializable {
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
            fieldValueTextField.selectAll();
            final ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, fieldValueTextField.getText());
            Clipboard.getSystemClipboard().setContent(content);

            KeyValue transparent = new KeyValue(clipboard_img.opacityProperty(), 0.0);
            KeyValue opaque = new KeyValue(clipboard_img.opacityProperty(), 1.0);

            final KeyFrame delayHideKf = new KeyFrame(Duration.ZERO, opaque);
            final KeyFrame hideKf = new KeyFrame(Duration.millis(500), transparent);
            Platform.runLater(new Timeline(delayHideKf, hideKf)::play);
        });
    }
}
