package org.faulty.wpreplace.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.faulty.wpreplace.models.Error;
import org.faulty.wpreplace.services.MissionService;
import org.faulty.wpreplace.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Log4j2
@Component
public class LoadMissionController {

    @Autowired
    private AbstractApplicationContext context;
    @Autowired
    private MissionService missionService;

    @FXML
    protected void loadFileAndContinue() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Mission File");
        FileChooser.ExtensionFilter mizFilter = new FileChooser.ExtensionFilter("Miz Files (*.miz)", "*.miz");
        fileChooser.getExtensionFilters().add(mizFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // File is selected, perform your logic here
            Error error = missionService.loadMission(selectedFile.getPath());
            if (error == null) {
                loadSelectRoute();
            } else {
                MessageUtils.showError("Error Loading Mission", "Failed to process the file\n" + error.message());
            }
        }
    }

    private void loadSelectRoute() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadGroup.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mission Details " + missionService.getMizFilePath());
            stage.setScene(new Scene(root, 1024, 768));
            App.addIcons(stage);
            stage.show();
        } catch (IOException e) {
            MessageUtils.showError("Error loading tab", "Check logs for more details");
            log.error("Error loading route data tab", e);
        }
    }
}