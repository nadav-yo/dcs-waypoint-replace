package org.faulty.wpreplace.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.faulty.wpreplace.models.Entry;
import org.faulty.wpreplace.services.MissionMizService;
import org.faulty.wpreplace.services.RouteContext;
import org.faulty.wpreplace.utils.MessageUtils;
import org.faulty.wpreplace.utils.RouteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CopyRouteController {
    @Autowired
    private RouteContext routeContext;
    @Autowired
    private MissionMizService missionContext;

    @FXML
    public Text routeDetails;

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
    public Button copyButton;

    public void initialize() {
        initCoalition();
        initCountryList();
        initUnitType();
        initGroupIds();
        routeDetails.setText("Source: " + routeContext.printDetails());
        selectBlueCoalition();
    }

    private void initCountryList() {
        countryIdListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectCountry());
        countryIdListView.setDisable(true);
    }

    private void initGroupIds() {
        groupIdListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectCountryGroup());
        groupIdListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupIdListView.setDisable(true);
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

    public void copyRouteData() {
        String coalition = ((RadioButton) coalitionToggleGroup.getSelectedToggle()).getUserData().toString();
        int countryId = countryIdListView.getSelectionModel().getSelectedItem().getLocation();
        List<Integer> groupIds = groupIdListView.getSelectionModel().getSelectedItems();
        String unitType = ((RadioButton) unitTypeToggleGroup.getSelectedToggle()).getUserData().toString();
        groupIds.forEach(group -> RouteUtils.setRoute(missionContext.getMission(), routeContext.getRoute(),
                coalition, countryId, unitType));
        String groups = groupIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String msg = String.format("Route set for coalition %s, country %d, unit '%s' with groups %s", coalition, countryId, unitType, groups);
        MessageUtils.showInfo("Route copied", msg);
        blueRadioButton.getScene().getWindow().hide();

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
            copyButton.setDisable(true);
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
        selectPlane();
    }


    private void selectCountryGroup() {
        copyButton.setDisable(false);
    }

    public void selectPlane() {
        selectUnit("plane");
    }

    private void selectHelicopter() {
        selectUnit("helicopter");
    }

    private void selectUnit(String unit) {
        String coalition = ((RadioButton) coalitionToggleGroup.getSelectedToggle()).getUserData().toString();
        int countryId = countryIdListView.getSelectionModel().getSelectedItem().getLocation();
        groupIdListView.getItems().clear();
        groupIdListView.getItems().addAll(RouteUtils.getSelectedGroupIds(missionContext.getMission(), coalition, countryId, unit));
        if (groupIdListView.getItems().isEmpty()) {
            copyButton.setDisable(true);
            return;
        }
        groupIdListView.getSelectionModel().select(0);
        groupIdListView.setDisable(false);
        selectCountryGroup();
    }
}
