package org.rafko.aidertool.appagent.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.rafko.aidertool.appagent.models.Stats;
import org.rafko.aidertool.appagent.services.StringUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class TagsEditorController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(TagsEditorController.class.getName());
    private static final double buttonIconSize = 30; /* 16 for the icon + some for the padding */
    private static final ImageView closeIcon = new ImageView(new Image("Img/close.png"));
    private static final String tagsRegex = "(?<!^) +";

    private final Consumer<List<String>> finalizeMethod;
    private final ArrayList<String> typedTagsList = new ArrayList<>();
    private final FilteredList<String> displayedTags;
    private final Stats stats;

    @FXML ListView<String> tagsListView;
    @FXML TextField tagsField;
    @FXML FlowPane tagsFlowPane;

    public TagsEditorController(Stats stats_, Consumer<List<String>> finalizeMethod_){
        stats = stats_;
        finalizeMethod = finalizeMethod_;
        displayedTags = new FilteredList<>(FXCollections.observableList(stats.getTags()));
        closeIcon.setFitWidth(16);
        closeIcon.setFitHeight(16);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Initialize available tags listview */
        displayedTags.predicateProperty().addListener((observable, oldValue, newValue) -> {
            tagsListView.setItems(displayedTags);
        });
        tagsField.textProperty().addListener((observable, oldValue, newValue) -> {
            String[] tags = newValue.split(tagsRegex);
            if((0 < tags.length)&&(!typedTagsList.contains(tags[tags.length-1])))
                displayedTags.setPredicate(s -> s.contains(tags[tags.length-1]));
        });
        tagsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if(-1 != newValue.intValue()){
                typedTagsList.add(tagsListView.getSelectionModel().getSelectedItem());
                tagsField.setText(generateTextFromTags());
                processTextField();
                tagsField.requestFocus();
                tagsField.positionCaret(tagsField.getLength()-1);
            }
        });

        /* Initialize the tag field */
        tagsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.trim().equals("")){
                tagsFlowPane.getChildren().clear();
                typedTagsList.clear();
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
                String[] tags = tagsFieldText.split(tagsRegex);
                if(
                    (keyEvent.getCode() == KeyCode.BACK_SPACE) /* Always recheck in case of backspace */
                    ||(typedTagsList.size() != tags.length) /* If the number of tags changed */
                ){ Platform.runLater(() -> processTextField(tags)); }
            }
        });
    }

    private String generateTextFromTags(){
        StringBuilder tagsSummary = new StringBuilder();
        for(String tag : typedTagsList){ /* Generate the text for every tag */
            double spacesNeeded = Math.max(0.0,(buttonIconSize)/Math.max(1.0,StringUtil.getStringWidth(" ",tagsField.getFont())));
            tagsSummary.append(tag);
            for(int i=0; i < spacesNeeded; ++i) tagsSummary.append(" ");
        }
        return tagsSummary.toString();
    }


    private void processTextField(){
        String tagsFieldText = tagsField.getText().substring(0,Math.min(45,tagsField.getText().length()));
        String[] tags = tagsFieldText.split(tagsRegex);
        processTextField(tags);
    }
    public void processTextField(String[] tags){
        tagsFlowPane.getChildren().clear();
        for(String tag : tags){ /* Generate a button for every tag */
            final double wordWidth = StringUtil.getStringWidth(tag, tagsField.getFont());
            Button btn = new Button(tag);
            btn.setOnAction(event -> {});
            btn.getStyleClass().add("tagBtn");
            btn.setPrefWidth(wordWidth + buttonIconSize);
            btn.setOnAction(event -> {
                typedTagsList.remove(tag);
                tagsFlowPane.getChildren().remove(btn);
                tagsField.setText(generateTextFromTags());
                tagsField.requestFocus();
                tagsField.positionCaret(tagsField.getLength()-1);
            });
            tagsFlowPane.getChildren().add(btn);
            btn.layout();
        }
        typedTagsList.clear();
        typedTagsList.addAll(Arrays.asList(tags));
        tagsField.setText(generateTextFromTags());
        tagsField.positionCaret(tagsField.getLength()-1);
    }

    public void finalise(){
        finalizeMethod.accept(typedTagsList); /* Consume the list of tags */
        ((Stage)tagsField.getScene().getWindow()).close(); /* Close the window */
    }
}
