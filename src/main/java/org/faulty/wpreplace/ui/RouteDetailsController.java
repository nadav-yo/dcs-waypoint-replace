package org.faulty.wpreplace.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import org.faulty.wpreplace.models.MapEntry;
import org.faulty.wpreplace.models.RouteDetails;
import org.faulty.wpreplace.services.ConfService;
import org.faulty.wpreplace.services.MissionService;
import org.faulty.wpreplace.services.RouteService;
import org.faulty.wpreplace.utils.RouteCanvasUtil;
import org.luaj.vm2.LuaValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RouteDetailsController {
    @FXML
    public ImageView mapImageView;
    @Autowired
    private AbstractApplicationContext context;
    @Autowired
    private ConfService confService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private MissionService missionService;
    @FXML
    private TableView<RouteDetails> dataTable;
    @FXML
    public Label coordinatesLabel;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public CheckBox showAll;
    @FXML
    public Canvas canvas;
    private boolean drawAllRouts;

    public void initialize() {
        MapEntry mapEntry = new MapEntry(missionService.getMapName());
        initRouteTable(mapEntry);
        initScrollPane(mapEntry);
        initCanvas(mapEntry);
        initDrawAllCheckBox(mapEntry);
    }

    private void initScrollPane(MapEntry mapEntry) {
        scrollPane.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                scrollPane.setPannable(true);
            }
        });
        scrollPane.setOnMouseReleased(event -> {
            if (!event.isMiddleButtonDown()) {
                scrollPane.setPannable(false);
            }
        });
        scrollPane.addEventFilter(ScrollEvent.ANY, event -> {
            double deltaY = event.getDeltaY();
            double scaleFactor = deltaY > 0 ? 1.1 : 0.9;
            int ratio = (int)(mapEntry.getRatio() * scaleFactor);
            mapEntry.setRatio(ratio);
            initCanvas(mapEntry);
            event.consume();
        });
        canvas.setUserData(scrollPane);

        // Display mouse coordinates on hover
        coordinatesLabel.setStyle("-fx-background-color: white; -fx-padding: 5px;");
        coordinatesLabel.setMouseTransparent(true);

        canvas.setOnMouseMoved(event -> {
            double mouseX = (event.getX() * mapEntry.getRatio()) + mapEntry.getMinX();
            double mouseY = (event.getY() * mapEntry.getRatio() + mapEntry.getMinY());

            coordinatesLabel.setText(String.format("X: %.2f, Y: %.2f, Zoom: %d", mouseX, mouseY, mapEntry.getRatio()));
        });
    }


    private void initCanvas(MapEntry mapEntry) {
        canvas.setHeight(mapEntry.getAdjustedHeight());
        canvas.setWidth(mapEntry.getAdjustedWidth());
        mapImageView.setFitHeight(mapEntry.getAdjustedHeight());
        mapImageView.setFitWidth(mapEntry.getAdjustedWidth());
        mapImageView.setImage(new Image(new File(confService.getDcsDirectory() + mapEntry.getFilePath()).toURI().toString(),
                mapEntry.getAdjustedWidth(), mapEntry.getAdjustedHeight(), true, true));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        // Set up scrolling

        if (drawAllRouts) {
            drawAllRoutes(gc, mapEntry);
        } else {
            drawSingleRoute(gc, mapEntry);
        }
    }

    private void initDrawAllCheckBox(MapEntry mapEntry) {
        showAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            if (newValue) {
                drawAllRouts = true;
                drawAllRoutes(gc, mapEntry);
            } else {
                drawAllRouts = false;
                drawSingleRoute(gc, mapEntry);
            }
        });
    }

    private void drawAllRoutes(GraphicsContext gc, MapEntry mapEntry) {
        int groupId = routeService.getGroupId();
        Map<Integer, LuaValue> friendlyGroupRoutes = routeService.getFriendlyGroupRoutes(missionService.getMission());
        Map<Integer, List<RouteDetails>> routes = friendlyGroupRoutes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, r -> RouteDetails.fromLuaRoute(r.getValue().checktable())));
        RouteCanvasUtil.drawRoute(gc, mapEntry, routes, groupId);
    }

    private void drawSingleRoute(GraphicsContext gc, MapEntry mapEntry) {
        List<RouteDetails> route = RouteDetails.fromLuaRoute(routeService.getRoute().get("points").checktable());
        int groupId = routeService.getGroupId();
        RouteCanvasUtil.drawRoute(gc, mapEntry, Map.of(groupId, route), groupId);
    }

    private void initRouteTable(MapEntry mapEntry) {
        List<RouteDetails> route = RouteDetails.fromLuaRoute(routeService.getRoute().get("points").checktable());
        ObservableList<RouteDetails> routes = FXCollections.observableArrayList(route);

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

        dataTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<RouteDetails> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    RouteCanvasUtil.jumpTo(canvas.getGraphicsContext2D(), mapEntry,
                            row.getItem().getX(),
                            row.getItem().getY());
                }
            });
            return row;
        });
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
