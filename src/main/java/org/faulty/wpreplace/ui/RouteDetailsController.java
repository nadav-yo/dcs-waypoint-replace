package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.faulty.wpreplace.models.RouteDetails;
import org.faulty.wpreplace.services.RouteService;
import org.luaj.vm2.LuaTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RouteDetailsController {
    @Autowired
    private AbstractApplicationContext context;
    @Autowired
    private RouteService routeService;
    @FXML
    private TableView<RouteDetails> dataTable;

    public void initialize() {
        LuaTable route = routeService.getRoute().get("points").checktable();
        ObservableList<RouteDetails> routes = FXCollections.observableArrayList(RouteDetails.fromLuaRoute(route));

        TableColumn<RouteDetails, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setEditable(false);

        TableColumn<RouteDetails, Double> xColumn = new TableColumn<>("X");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        xColumn.setOnEditCommit(event -> {
            RouteDetails editRoute = event.getRowValue();
            editRoute.setX(event.getNewValue());
        });

        TableColumn<RouteDetails, Double> yColumn = new TableColumn<>("Y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        yColumn.setOnEditCommit(event -> {
            RouteDetails editRoute = event.getRowValue();
            editRoute.setY(event.getNewValue());
        });

        TableColumn<RouteDetails, Integer> altColumn = new TableColumn<>("Alt");
        altColumn.setCellValueFactory(new PropertyValueFactory<>("alt"));
        altColumn.setOnEditCommit(event -> {
            RouteDetails editRoute = event.getRowValue();
            editRoute.setAlt(event.getNewValue());
        });

        TableColumn<RouteDetails, Double> speedColumn = new TableColumn<>("Speed");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));
        speedColumn.setOnEditCommit(event -> {
            RouteDetails editRoute = event.getRowValue();
            editRoute.setSpeed(event.getNewValue());
        });

        TableColumn<RouteDetails, Double> etaColumn = new TableColumn<>("ETA");
        etaColumn.setCellValueFactory(new PropertyValueFactory<>("eta"));
        etaColumn.setEditable(false);

        TableColumn<RouteDetails, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setEditable(false);

        TableColumn<RouteDetails, String> taskColumn = new TableColumn<>("Task");
        taskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        taskColumn.setEditable(false);

        dataTable.getColumns().addAll(idColumn, xColumn, yColumn, altColumn, speedColumn, etaColumn, typeColumn, taskColumn);
        dataTable.setEditable(true);

        dataTable.setItems(routes);
        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        dataTable.getSortOrder().add(idColumn);
        dataTable.sort();
    }

    public void saveChanges() {
        routeService.updateRoute(dataTable.getItems());
    }

    public void copyRoute() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CopyRoute.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Copy Route to destinations");
            stage.setScene(new Scene(root, 400, 300));
            App.addIcons(stage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
