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
import org.faulty.wpreplace.models.SlimRoute;
import org.faulty.wpreplace.services.RouteContext;
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
    private RouteContext routeContext;
    @FXML
    private TableView<SlimRoute> dataTable;

    public void initialize() {
        LuaTable route = routeContext.getRoute().get("points").checktable();
        ObservableList<SlimRoute> routes = FXCollections.observableArrayList(SlimRoute.fromLuaRoute(route));

        TableColumn<SlimRoute, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setEditable(false);

        TableColumn<SlimRoute, Double> xColumn = new TableColumn<>("X");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        xColumn.setOnEditCommit(event -> {
            SlimRoute editRoute = event.getRowValue();
            editRoute.setX(event.getNewValue());
        });

        TableColumn<SlimRoute, Double> yColumn = new TableColumn<>("Y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        yColumn.setOnEditCommit(event -> {
            SlimRoute editRoute = event.getRowValue();
            editRoute.setY(event.getNewValue());
        });

        TableColumn<SlimRoute, Integer> altColumn = new TableColumn<>("Alt");
        altColumn.setCellValueFactory(new PropertyValueFactory<>("alt"));
        altColumn.setOnEditCommit(event -> {
            SlimRoute editRoute = event.getRowValue();
            editRoute.setAlt(event.getNewValue());
        });

        TableColumn<SlimRoute, Double> speedColumn = new TableColumn<>("Speed");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));
        speedColumn.setOnEditCommit(event -> {
            SlimRoute editRoute = event.getRowValue();
            editRoute.setSpeed(event.getNewValue());
        });

        TableColumn<SlimRoute, Double> etaColumn = new TableColumn<>("ETA");
        etaColumn.setCellValueFactory(new PropertyValueFactory<>("eta"));
        etaColumn.setEditable(false);

        dataTable.getColumns().addAll(idColumn, xColumn, yColumn, altColumn, speedColumn, etaColumn);
        dataTable.setEditable(true);

        dataTable.setItems(routes);
        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        dataTable.getSortOrder().add(idColumn);
        dataTable.sort();
    }

    public void saveChanges() {
        routeContext.updateRoute(dataTable.getItems());
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
