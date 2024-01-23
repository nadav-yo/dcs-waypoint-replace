package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.faulty.wpreplace.models.UnitDataLink;

import java.util.List;

public class DataLinkController {
    @FXML
    public Label typeLabel;
    @FXML
    public TableView<UnitDataLink.Item> settingsTable;
    @FXML
    public TableView<UnitDataLink.Contrib> teamMembersTable;
    @FXML
    public TableView<UnitDataLink.Contrib> donorsTable;


    public void initialize(UnitDataLink data) {
        typeLabel.setText("Type: " + data.getType());
        initSettings(data);
        initContrib(teamMembersTable, data.getLink16TeamMembers());
        initContrib(donorsTable, data.getLink16Donors());


    }

    private void initSettings(UnitDataLink data) {
        TableColumn<UnitDataLink.Item, String> settingColumn = new TableColumn<>("Setting");
        settingColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        TableColumn<UnitDataLink.Item, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        settingsTable.getColumns().addAll(settingColumn, valueColumn);
        settingsTable.setItems(FXCollections.observableArrayList(data.getLink16Settings()));
    }

    private void initContrib(TableView<UnitDataLink.Contrib> teamMembersTable, List<UnitDataLink.Contrib> data) {
        TableColumn<UnitDataLink.Contrib, String> idColumn = new TableColumn<>("Team Member");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UnitDataLink.Contrib, Integer> unitIdColumn = new TableColumn<>("Mission Unit ID");
        unitIdColumn.setCellValueFactory(new PropertyValueFactory<>("missionUnitId"));
        TableColumn<UnitDataLink.Contrib, Boolean> tdoaColumn = new TableColumn<>("TDOA");
        tdoaColumn.setCellValueFactory(new PropertyValueFactory<>("TDOA"));
        teamMembersTable.getColumns().addAll(idColumn, unitIdColumn, tdoaColumn);
        teamMembersTable.setItems(FXCollections.observableArrayList(data));
    }
}
