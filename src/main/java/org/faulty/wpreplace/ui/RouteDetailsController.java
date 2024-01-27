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
import org.faulty.wpreplace.utils.RouteUtils;
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
    @FXML
    public ImageView mapImageView;
    @FXML
    public ComboBox<Integer> groupSelect;
    private Map<Integer, List<RouteDetails>> allGroupRoutes;
    private boolean drawAllRouts;

    public void initialize() {
        MapEntry mapEntry = new MapEntry(missionService.getMapName());
        setAllGroups();
        initGroupSelect(mapEntry);
        initScrollPane(mapEntry);
        initCanvas(mapEntry);
        initRouteTable(mapEntry);
        initDrawAllCheckBox(mapEntry);
    }

    private void setAllGroups() {
        Map<Integer, LuaValue> friendlyGroupRoutes = routeService.getFriendlyGroupRoutes(missionService.getMission());
        allGroupRoutes = friendlyGroupRoutes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, r -> RouteDetails.fromLuaRoute(r.getValue().checktable())));
    }

    private void initGroupSelect(MapEntry mapEntry) {
        groupSelect.getItems().addAll(RouteUtils.getSelectedGroupIds(missionService.getMission(),
                routeService.getCoalition(), routeService.getCountryId(), routeService.getUnitType()));
        groupSelect.setValue(routeService.getGroupId());
        groupSelect.setOnAction(event -> setRouteDetails(mapEntry));
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
            double scaleFactor = deltaY > 0 ? 0.9 : 1.1;
            int ratio = (int) (mapEntry.getRatio() * scaleFactor);
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

        drawRoutes(mapEntry, gc);
    }

    private void initDrawAllCheckBox(MapEntry mapEntry) {
        showAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            drawAllRouts = newValue;
            drawRoutes(mapEntry, gc);
        });
    }

    private void drawRoutes(MapEntry mapEntry, GraphicsContext gc) {
        if (drawAllRouts) {
            drawAllRoutes(gc, mapEntry);
        } else {
            drawSingleRoute(gc, mapEntry);
        }
    }

    private void drawAllRoutes(GraphicsContext gc, MapEntry mapEntry) {
        RouteCanvasUtil.drawRoute(gc, mapEntry, allGroupRoutes, groupSelect.getValue());
    }

    private void drawSingleRoute(GraphicsContext gc, MapEntry mapEntry) {
        int groupId = groupSelect.getValue();
        List<RouteDetails> route = allGroupRoutes.get(groupId);
        RouteCanvasUtil.drawRoute(gc, mapEntry, Map.of(groupId, route), groupId);
    }

    private void initRouteTable(MapEntry mapEntry) {
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

        setRouteDetails(mapEntry);
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

    private void setRouteDetails(MapEntry mapEntry) {
        List<RouteDetails> route = allGroupRoutes.get(groupSelect.getValue());
        ObservableList<RouteDetails> routes = FXCollections.observableArrayList(route);
        dataTable.getItems().clear();
        dataTable.setItems(routes);
        drawRoutes(mapEntry, canvas.getGraphicsContext2D());
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
