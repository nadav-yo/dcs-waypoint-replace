package org.faulty.wpreplace.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.faulty.wpreplace.models.Error;
import org.faulty.wpreplace.services.MissionMizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class LoadMissionController {

    @Autowired
    private AbstractApplicationContext context ;
    @Autowired
    private MissionMizService missionMizService;
    @FXML
    private Text messageText;

    @FXML
    protected void loadFileAndContinue() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Mission File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // File is selected, perform your logic here
            Error error =  missionMizService.loadMission(selectedFile.getPath());
            if (error == null) {
                loadSelectRoute();
            } else {
                messageText.setText("Failed to process the file - " + error.message());
            }
        } else {
            messageText.setText("No file selected.");
        }
    }

    private void loadSelectRoute() {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadGroup.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Source Details");
        stage.setScene(new Scene(root, 800, 600));
        Stage currentStage = (Stage) messageText.getScene().getWindow();
        App.addIcons(stage);
        currentStage.hide();

        stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}