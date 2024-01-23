package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;
import org.faulty.wpreplace.models.UnitPayload;

import java.util.List;

public class PayloadController {
    @FXML
    public Label typeLabel;
    @FXML
    public TableView<Item> dataTable;
    @FXML
    private ListView<String> pylonsListView;


    public void initialize(UnitPayload data) {
        typeLabel.setText("Type: " + data.getType());

        TableColumn<Item, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Item, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dataTable.getColumns().addAll(typeColumn, quantityColumn);
        dataTable.setItems(FXCollections.observableArrayList(List.of(
                new Item("Fuel", data.getFuel()),
                new Item("Flare", data.getFlare()),
                new Item("Chaff", data.getChaff()),
                new Item("Gun", data.getGun()),
                new Item("Ammo Type", data.getAmmoType()))));

        pylonsListView.getItems().addAll(data.getPylons());
    }

    @Data
    public static final class Item {
        private final String type;
        private final int quantity;
    }
}
