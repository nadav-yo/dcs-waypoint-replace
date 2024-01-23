package org.faulty.wpreplace.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.faulty.wpreplace.models.Entry;
import org.faulty.wpreplace.models.Error;
import org.faulty.wpreplace.services.MissionMizService;
import org.faulty.wpreplace.services.RouteContext;
import org.faulty.wpreplace.utils.MessageUtils;
import org.faulty.wpreplace.utils.RouteUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class LoadGroupController {
    @Autowired
    private AbstractApplicationContext context;
    @Autowired
    private RouteContext routeContext;
    @Autowired
    private MissionMizService missionContext;

    // coalition
    public RadioButton blueRadioButton;
    public RadioButton redRadioButton;
    private ToggleGroup coalitionToggleGroup;

    // country
    @FXML
    public ListView<Entry> countryIdListView;
    // group
    @FXML
    public ListView<Integer> groupIdListView;
    // unit type
    public RadioButton planeRadioButton;
    public RadioButton helicopterRadioButton;
    private ToggleGroup unitTypeToggleGroup;

    // buttons
    @FXML
    public Button groupDetails;
    @FXML
    public Button loadButton;
    @FXML
    public Button saveButton;

    public void initialize() {
        initCoalition();
        initCountryList();
        initUnitType();
        initGroupIds();
        groupDetails.setDisable(true);
        loadButton.setDisable(true);
        saveButton.setDisable(true);
        selectBlueCoalition();
    }

    private void initCountryList() {
        countryIdListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectCountry());
        countryIdListView.setDisable(true);
    }

    private void initGroupIds() {
        groupIdListView.setDisable(true);
        groupIdListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectCountryGroup());
    }

    private void initUnitType() {
        unitTypeToggleGroup = new ToggleGroup();
        planeRadioButton.setToggleGroup(unitTypeToggleGroup);
        helicopterRadioButton.setToggleGroup(unitTypeToggleGroup);
        planeRadioButton.setDisable(true);
        helicopterRadioButton.setDisable(true);
        if (!unitTypeToggleGroup.getToggles().isEmpty()) {
            unitTypeToggleGroup.selectToggle(unitTypeToggleGroup.getToggles().get(0));
        }
        planeRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectPlane();
            }
        });
        helicopterRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectHelicopter();
            }
        });
    }

    private void initCoalition() {
        coalitionToggleGroup = new ToggleGroup();
        blueRadioButton.setToggleGroup(coalitionToggleGroup);
        redRadioButton.setToggleGroup(coalitionToggleGroup);
        if (!coalitionToggleGroup.getToggles().isEmpty()) {
            coalitionToggleGroup.selectToggle(coalitionToggleGroup.getToggles().get(0));
        }
        blueRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectBlueCoalition();
            }
        });
        redRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectRedCoalition();
            }
        });
    }

    @FXML
    protected void loadData() {
        String coalition = ((RadioButton) coalitionToggleGroup.getSelectedToggle()).getUserData().toString();
        int countryId = countryIdListView.getSelectionModel().getSelectedItem().getLocation();
        int groupId = groupIdListView.getSelectionModel().getSelectedItem();
        String unitType = ((RadioButton) unitTypeToggleGroup.getSelectedToggle()).getUserData().toString();
        LuaTable groups = RouteUtils.getGroups(missionContext.getMission(), coalition, countryId, unitType);
        if (groups == null) {
            MessageUtils.showError("Error!", "Group not found!");
        } else {
            LuaValue route = groups.get(groupId).get("route");
            routeContext.setRoute(route, coalition, countryId, unitType, groupId);
            saveButton.setDisable(false);
            loadRouteData();
        }
    }

    private void selectBlueCoalition() {
        setCoalitionCounties("blue");
    }

    private void selectRedCoalition() {
        setCoalitionCounties("red");
    }

    private void setCoalitionCounties(String coalition) {
        countryIdListView.getItems().clear();
        countryIdListView.getItems().addAll(RouteUtils.getAllCoalitionCountries(missionContext.getMission(), coalition));
        if (countryIdListView.getItems().isEmpty()) {
            groupDetails.setDisable(true);
            loadButton.setDisable(true);
            saveButton.setDisable(true);
            return;
        }
        countryIdListView.getSelectionModel().select(0);
        countryIdListView.setDisable(false);
        selectCountry();
    }

    private void selectCountry() {
        if (unitTypeToggleGroup.getSelectedToggle() == null) {
            planeRadioButton.selectedProperty().setValue(true);
        }
        planeRadioButton.setDisable(false);
        helicopterRadioButton.setDisable(false);
        selectUnit(unitTypeToggleGroup.getSelectedToggle().getUserData().toString());
    }


    private void selectCountryGroup() {
        loadButton.setDisable(false);
        groupDetails.setDisable(false);
    }

    public void selectPlane() {
        selectUnit("plane");
    }

    private void selectHelicopter() {
        selectUnit("helicopter");
    }

    private void selectUnit(String unit) {
        String coalition = ((RadioButton) coalitionToggleGroup.getSelectedToggle()).getUserData().toString();
        if (countryIdListView.getSelectionModel().getSelectedItem() == null) {
            if (countryIdListView.getSelectionModel().getSelectedItems().isEmpty()) {
                groupIdListView.getItems().clear();
                return;
            }
            countryIdListView.getSelectionModel().select(0);
        }
        int countryId = countryIdListView.getSelectionModel().getSelectedItem().getLocation();
        groupIdListView.getItems().clear();
        groupIdListView.getItems().addAll(RouteUtils.getSelectedGroupIds(missionContext.getMission(), coalition, countryId, unit));
        if (groupIdListView.getItems().isEmpty()) {
            groupDetails.setDisable(true);
            loadButton.setDisable(true);
            saveButton.setDisable(true);
            return;
        }
        groupIdListView.getSelectionModel().select(0);
        groupIdListView.setDisable(false);
        selectCountryGroup();
    }

    public void loadRouteData() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RouteDetails.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Route Details for " + routeContext.printDetails());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Mission File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // File is selected, perform your logic here
            String path = selectedFile.getPath();
            Error error = missionContext.saveMission(path);
            if (error == null) {
                MessageUtils.showInfo("Mission Saved", "Mission Successfully saved under " + path);
            } else {
                MessageUtils.showError("Error!", "Failed to process the file - " + error.message());
            }
        } else {
            MessageUtils.showError("Error!", "No file selected.");
        }
    }

    public void loadGroup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupDetails.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            GroupDetailsController groupDetailsController = loader.getController();
            String coalition = ((RadioButton) coalitionToggleGroup.getSelectedToggle()).getUserData().toString();
            int countryId = countryIdListView.getSelectionModel().getSelectedItem().getLocation();
            String unitType = ((RadioButton) unitTypeToggleGroup.getSelectedToggle()).getUserData().toString();
            int groupId = groupIdListView.getSelectionModel().getSelectedItem();
            groupDetailsController.setGroup(coalition, countryId, unitType, groupId);
            Stage stage = new Stage();
            stage.setTitle("Group Details");
            stage.setScene(new Scene(root, 1024, 600));
            App.addIcons(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
