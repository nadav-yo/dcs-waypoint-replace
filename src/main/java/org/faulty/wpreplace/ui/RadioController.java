package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.faulty.wpreplace.models.UnitRadio;

import java.util.List;

public class RadioController {
    @FXML
    public Label typeLabel;

    @FXML
    private TableView<UnitRadio.Channel> radio1ListView;
    @FXML
    private TableView<UnitRadio.Channel> radio2ListView;


    public void initialize(UnitRadio data) {
        typeLabel.setText("Type: " + data.getType());

        addRadioChannels(radio1ListView, data.getChannels1());
        addRadioChannels(radio2ListView, data.getChannels2());
    }

    private void addRadioChannels(TableView<UnitRadio.Channel> tableView, List<UnitRadio.Channel> channels) {
        TableColumn<UnitRadio.Channel, Integer> indexColumn = new TableColumn<>("Channel #");
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
        TableColumn<UnitRadio.Channel, Integer> freqColumn = new TableColumn<>("Channel Freq");
        freqColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        tableView.getColumns().addAll(indexColumn, freqColumn);
        tableView.setItems(FXCollections.observableArrayList(channels));
    }
}
