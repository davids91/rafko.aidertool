package org.rafko.aidertool.appagent.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import org.rafko.aidertool.appagent.services.StringUtil;

import javax.swing.text.html.HTML;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagsEditorController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(TagsEditorController.class.getName());
    final double buttonIconSize = 30; /* 16 for the icon + some for the padding */
    private static final ImageView closeIcon = new ImageView(new Image("Img/close.png"));

    @FXML TextField tagsField;
    @FXML FlowPane tagsFlowPane;

    private final ArrayList<String> tagList = new ArrayList<>();

    public TagsEditorController(){
        closeIcon.setFitWidth(16);
        closeIcon.setFitHeight(16);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tagsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.trim().equals("")){
                tagsFlowPane.getChildren().clear();
                tagList.clear();
            }
        });
        tagsField.setOnMouseClicked(event -> tagsField.positionCaret(tagsField.getLength()-1));
        tagsFlowPane.setOnMouseClicked(event -> {
            tagsField.requestFocus();
            tagsField.positionCaret(tagsField.getLength()-1);
        });
        tagsField.setOnKeyReleased(keyEvent -> {
            String tagsFieldText = tagsField.getText().substring(0,Math.min(45,tagsField.getText().length()));
            if(
                ((keyEvent.getCode() == KeyCode.SPACE)||(keyEvent.getCode() == KeyCode.BACK_SPACE))
                &&(0 < tagsFieldText.trim().length()) /* If there is more in the text, than whitespace */
                &&(tagsFieldText.lastIndexOf(" ") == tagsField.getText().length()-1) /* if the last character is space */
            ){
                String[] tags = tagsFieldText.split("(?<!^) +");
                if(
                    (keyEvent.getCode() == KeyCode.BACK_SPACE) /* Always recheck in case of backspace */
                    ||(tagList.size() != tags.length) /* If the number of tags changed */
                ){
                    Platform.runLater(() -> {
                        tagsFlowPane.getChildren().clear();
                        for(String tag : tags){ /* Generate a button for every tag */
                            final double wordWidth = StringUtil.getStringWidth(tag, tagsField.getFont());
                            Button btn = new Button(tag);
                            btn.setOnAction(event -> {});
                            btn.getStyleClass().add("tagBtn");
                            btn.setPrefWidth(wordWidth + buttonIconSize);
                            btn.setOnAction(event -> {
                                tagList.remove(tag);
                                tagsFlowPane.getChildren().remove(btn);
                                tagsField.setText(GenerateTextFromTags());
                                tagsField.requestFocus();
                                tagsField.positionCaret(tagsField.getLength()-1);
                            });
                            tagsFlowPane.getChildren().add(btn);
                            btn.layout();
                        }
                        tagList.clear();
                        tagList.addAll(Arrays.asList(tags));
                        tagsField.setText(GenerateTextFromTags());
                        tagsField.positionCaret(tagsField.getLength()-1);
                    });
                }
            }
        });
    }

    private String GenerateTextFromTags(){
        StringBuilder tagsSummary = new StringBuilder();
        for(String tag : tagList){ /* Generate the text for every tag */
            double spacesNeeded = Math.max(0.0, /* TODO: use actual size of the button, instead of known prefWidth */
                (buttonIconSize)/Math.max(1.0,StringUtil.getStringWidth(" ",tagsField.getFont()))
            );
            tagsSummary.append(tag);
            for(int i=0; i < spacesNeeded; ++i) tagsSummary.append(" ");
        }
        return tagsSummary.toString();
    }
}
