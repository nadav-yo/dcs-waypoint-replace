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
import org.faulty.wpreplace.services.RouteContext;
import org.faulty.wpreplace.models.SlimRoute;
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
        ObservableList<SlimRoute> routes = FXCollections.observableArrayList(SlimRoute.fromLuaRuote(route));
        TableColumn<SlimRoute, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<SlimRoute, Float> xColumn = new TableColumn<>("X");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));

        TableColumn<SlimRoute, Float> yColumn = new TableColumn<>("Y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));

        TableColumn<SlimRoute, Integer> altColumn = new TableColumn<>("Alt");
        altColumn.setCellValueFactory(new PropertyValueFactory<>("alt"));
        dataTable.getColumns().addAll(idColumn, xColumn, yColumn, altColumn);

        dataTable.setItems(routes);
        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        dataTable.getSortOrder().add(idColumn);
        dataTable.sort();
    }

    public void copyRoute() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CopyRoute.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Copy Route to destinations");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
