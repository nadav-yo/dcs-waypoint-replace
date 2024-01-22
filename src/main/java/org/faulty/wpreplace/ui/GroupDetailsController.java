package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j2;
import org.faulty.wpreplace.models.UnitDetails;
import org.faulty.wpreplace.models.UnitPayload;
import org.faulty.wpreplace.services.MissionMizService;
import org.faulty.wpreplace.utils.MessageUtils;
import org.faulty.wpreplace.utils.RouteUtils;
import org.luaj.vm2.LuaTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Log4j2
@Component
public class GroupDetailsController {
    @Autowired
    private MissionMizService missionMizService;
    @FXML
    public Text groupDetailText;
    @FXML
    private TableView<UnitDetails> dataTable;

    public void initialize() {

    }

    public void setGroup(String coalition, int countryId, String unitType, int groupId) {
        LuaTable group = RouteUtils.getGroup(missionMizService.getMission(), coalition, countryId, unitType, groupId);
        if (group == null) {
            MessageUtils.showError("Error!", "Unable to load group");
            return;
        }
        groupDetailText.setText(String.format("Showing Group info for: coalition %s, country %d, unitType %s group %d ",
                coalition, countryId, unitType, groupId));
        ObservableList<UnitDetails> routes = FXCollections.observableArrayList(UnitDetails.fromLuaGroup(group));
        TableColumn<UnitDetails, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<UnitDetails, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<UnitDetails, String> skillColumn = new TableColumn<>("Skill");
        skillColumn.setCellValueFactory(new PropertyValueFactory<>("skill"));
        TableColumn<UnitDetails, String> callSignsColumn = new TableColumn<>("CallSigns");
        callSignsColumn.setCellValueFactory(new PropertyValueFactory<>("callSigns"));
        TableColumn<UnitDetails, String> unitColumn = new TableColumn<>("Unit");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        TableColumn<UnitDetails, Float> xColumn = new TableColumn<>("X");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        TableColumn<UnitDetails, Float> yColumn = new TableColumn<>("Y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        TableColumn<UnitDetails, Integer> altColumn = new TableColumn<>("Alt");
        altColumn.setCellValueFactory(new PropertyValueFactory<>("alt"));
        TableColumn<UnitDetails, BigDecimal> headingColumn = new TableColumn<>("Heading");
        headingColumn.setCellValueFactory(new PropertyValueFactory<>("heading"));
        TableColumn<UnitDetails, Float> speedColumn = new TableColumn<>("Speed");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));

        TableColumn<UnitDetails, Void> viewColumn = new TableColumn<>("View Payload");
        viewColumn.setCellFactory(createButtonCellFactory());

        dataTable.getColumns().addAll(idColumn, nameColumn, skillColumn, callSignsColumn, unitColumn, xColumn, yColumn, altColumn, headingColumn, speedColumn, viewColumn);

        dataTable.setItems(routes);
        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        dataTable.getSortOrder().add(idColumn);
        dataTable.sort();
    }

    private Callback<TableColumn<UnitDetails, Void>, TableCell<UnitDetails, Void>> createButtonCellFactory() {
        return (param) -> new TableCell<>() {
            private final Button viewButton = new Button("View Payload");

            {
                viewButton.setOnAction(event -> {
                    UnitDetails data = getTableView().getItems().get(getIndex());
                    handleViewButtonClick(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        };
    }

    private void handleViewButtonClick(UnitDetails data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PayloadDetails.fxml"));
            Parent root = loader.load();

            UnitPayload unitPayload = UnitPayload.fromLuaGroup(data.getLuaUnit());
            PayloadController detailsController = loader.getController();
            detailsController.initialize(unitPayload);

            Stage stage = new Stage();
            stage.setTitle("Payload for " + data.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 800, 600));
            stage.showAndWait();
        } catch (IOException e) {
            log.error("Error loading payload", e);
            MessageUtils.showError("Error!", "Error loading payload. Check logs for more info.");
        }
    }
}
