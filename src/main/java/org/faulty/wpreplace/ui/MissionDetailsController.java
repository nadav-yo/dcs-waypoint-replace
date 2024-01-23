package org.faulty.wpreplace.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.faulty.wpreplace.models.Entry;
import org.faulty.wpreplace.models.Error;
import org.faulty.wpreplace.services.MissionService;
import org.faulty.wpreplace.services.RouteService;
import org.faulty.wpreplace.utils.MessageUtils;
import org.faulty.wpreplace.utils.RouteUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Log4j2
@Component
public class MissionDetailsController {
    @Autowired
    private AbstractApplicationContext context;
    @Autowired
    private RouteService routeService;
    @Autowired
    private MissionService missionContext;
    @FXML
    public TabPane tabPane;
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
        loadMissionDetails();
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
            routeService.setRoute(route, coalition, countryId, unitType, groupId);
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
            SplitPane routePane = loader.load();
            Tab tab = new Tab("Route " + routeService.printDetails());
            tab.setContent(routePane);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            MessageUtils.showError("Error loading tab", "Check logs for more details");
            log.error("Error loading route data tab", e);
        }
    }

    public void saveData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Mission File");
        fileChooser.setInitialFileName("newMission.miz");
        fileChooser.setInitialDirectory(new File(missionContext.getMizFilePath()).getParentFile());
        FileChooser.ExtensionFilter mizFilter = new FileChooser.ExtensionFilter("Miz Files (*.miz)", "*.miz");
        fileChooser.getExtensionFilters().add(mizFilter);
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
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
            SplitPane groupPane = loader.load();
            GroupDetailsController groupDetailsController = loader.getController();
            String coalition = ((RadioButton) coalitionToggleGroup.getSelectedToggle()).getUserData().toString();
            int countryId = countryIdListView.getSelectionModel().getSelectedItem().getLocation();
            String unitType = ((RadioButton) unitTypeToggleGroup.getSelectedToggle()).getUserData().toString();
            int groupId = groupIdListView.getSelectionModel().getSelectedItem();
            groupDetailsController.setGroup(coalition, countryId, unitType, groupId);

            Tab tab = new Tab("Group Details #" + groupId);
            tab.setContent(groupPane);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            MessageUtils.showError("Error loading tab", "Check logs for more details");
            log.error("Error loading route data tab", e);
        }
    }

    public void loadMissionDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneralMissionDetails.fxml"));
            loader.setControllerFactory(context::getBean);
            SplitPane groupPane = loader.load();
            Tab tab = new Tab("Mission Details");
            tab.setContent(groupPane);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            MessageUtils.showError("Error loading tab", "Check logs for more details");
            log.error("Error loading mission details tab", e);
        }
    }
}
