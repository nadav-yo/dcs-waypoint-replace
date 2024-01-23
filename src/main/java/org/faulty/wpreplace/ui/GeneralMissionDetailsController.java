package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import lombok.extern.log4j.Log4j2;
import org.faulty.wpreplace.models.MissionDetails;
import org.faulty.wpreplace.models.MissionWeather;
import org.faulty.wpreplace.services.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class GeneralMissionDetailsController {
    @Autowired
    private MissionService missionService;

    @FXML
    public Text dateText;
    @FXML
    public Text theatreText;

    @FXML
    private TableView<MissionWeather.Item> dataTable;
    @FXML
    public TableView<MissionWeather.Wind> windTable;
    @FXML
    public TableView<MissionWeather.Item> cloudsTable;

    public void initialize() {
        MissionDetails missionDetails = MissionDetails.fromLuaMission(missionService.getMission());
        dateText.setText(missionDetails.getDate().toString());
        theatreText.setText(missionDetails.getTheatre());
        initGeneralDate(missionDetails);
        initWindTable(missionDetails);
        initCloudsTable(missionDetails);


    }

    private void initCloudsTable(MissionDetails missionDetails) {
        TableColumn<MissionWeather.Item, String> keyColumn = new TableColumn<>("Key");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));

        TableColumn<MissionWeather.Item, String> valColumn = new TableColumn<>("Value");
        valColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        cloudsTable.getColumns().addAll(keyColumn, valColumn);
        cloudsTable.getItems().addAll(missionDetails.getWeather().getClouds());
    }

    private void initWindTable(MissionDetails missionDetails) {
        TableColumn<MissionWeather.Wind, String> altColumn = new TableColumn<>("Altitude");
        altColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<MissionWeather.Wind, String> speedColumn = new TableColumn<>("Speed");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));

        TableColumn<MissionWeather.Wind, String> dirColumn = new TableColumn<>("Direction");
        dirColumn.setCellValueFactory(new PropertyValueFactory<>("dir"));

        windTable.getColumns().addAll(altColumn, speedColumn, dirColumn);
        windTable.getItems().addAll(missionDetails.getWeather().getWinds());
    }

    private void initGeneralDate(MissionDetails missionDetails) {
        TableColumn<MissionWeather.Item, String> typeColumn = new TableColumn<>("Key");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("key"));

        TableColumn<MissionWeather.Item, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        dataTable.getColumns().addAll(typeColumn, valueColumn);
        MissionWeather weather = missionDetails.getWeather();
        dataTable.setItems(FXCollections.observableArrayList(List.of(
                new MissionWeather.Item("Name", weather.getName()),
                new MissionWeather.Item("Atmosphere Type", weather.getAtmosphereType()),
                new MissionWeather.Item("Enable Fog", weather.isEnableFog()),
                new MissionWeather.Item("Fog Thickness", weather.getFogThickness()),
                new MissionWeather.Item("Fog Visibility", weather.getFogVisibility()),
                new MissionWeather.Item("Season Temperature", weather.getSeasonTemperature())
        )));
    }
}
