package org.faulty.wpreplace.utils;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.faulty.wpreplace.models.MapEntry;
import org.faulty.wpreplace.models.RouteDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class RouteCanvasUtil {
    public static void drawRoute(GraphicsContext gc, MapEntry mapEntry, Map<Integer, List<RouteDetails>> routesList, int groupId) {
        for (var route : routesList.entrySet()) {
            if (!route.getValue().isEmpty()) {
                RouteDraw routeDraw = getRouteDraw(route.getValue(), mapEntry, route.getKey() == groupId);
                routeDraw.lines.forEach(line -> RouteCanvasUtil.drawLine(gc, line));
                routeDraw.dots.forEach(dot -> RouteCanvasUtil.drawDot(gc, dot));
                routeDraw.labels.forEach(label -> RouteCanvasUtil.drawLabel(gc, label));
                jumpTo(gc, mapEntry, routeDraw.firstX, routeDraw.firstY);
            }
        }
    }

    private static RouteDraw getRouteDraw(List<RouteDetails> route, MapEntry mapEntry, boolean isSelected) {
        List<Dot> dots = new ArrayList<>();
        List<Label> labels = new ArrayList<>();
        List<Line> lines = new ArrayList<>();

        RouteDetails prev = route.get(0);
        double srcX = (prev.getX() - mapEntry.getMinX()) / mapEntry.getRatio();
        double srcY = mapEntry.getAdjustedHeight() - (prev.getY() - mapEntry.getMinY()) / mapEntry.getRatio();
        dots.add(new Dot(srcX, srcY, isSelected ? Color.RED : Color.CORAL));
        labels.add(new Label(String.valueOf(prev.getId()), srcX, srcY, isSelected ? Color.BLACK : Color.CORAL));
        for (int i = 1; i < route.size(); i++) {
            RouteDetails curr = route.get(i);
            double destX = (curr.getX() - mapEntry.getMinX()) / mapEntry.getRatio();
            double destY = mapEntry.getAdjustedHeight() - (curr.getY() - mapEntry.getMinY()) / mapEntry.getRatio();
            lines.add(new Line(srcX, srcY, destX, destY, isSelected ? Color.BLUE : Color.YELLOWGREEN));
            Dot dot = new Dot(destX, destY, Color.RED);
            dots.add(dot);
            addLabel(labels, new Label(String.valueOf(curr.getId()), destX, destY, isSelected ? Color.BLACK : Color.CORAL));
            srcX = destX;
            srcY = destY;
        }
        return new RouteDraw(lines, dots, labels, prev.getX(), prev.getY());
    }

    public static void jumpTo(GraphicsContext gc, MapEntry mapEntry, double x, double y) {
        Canvas canvas = gc.getCanvas();
        ScrollPane scrollPane = (ScrollPane) canvas.getUserData();
        double mapX = (x - mapEntry.getMinX()) / mapEntry.getRatio();
        double mapY = mapEntry.getAdjustedHeight() - (y - mapEntry.getMinY()) / mapEntry.getRatio();
        double hValue = (mapX - 0.5 * scrollPane.getViewportBounds().getWidth()) / (canvas.getWidth() - scrollPane.getViewportBounds().getWidth());
        double vValue = (mapY - 0.5 * scrollPane.getViewportBounds().getHeight()) / (canvas.getHeight() - scrollPane.getViewportBounds().getHeight());
        Platform.runLater(() -> {
            scrollPane.setHvalue(Math.max(0, Math.min(1, hValue)));
            scrollPane.setVvalue(Math.max(0, Math.min(1, vValue)));
        });

    }

    private static void addLabel(List<Label> labels, Label label) {
        int idx = labels.indexOf(label);
        if (idx > -1) {
            labels.get(idx).label += ("," + label.label);
        } else {
            labels.add(label);
        }
    }

    private static void drawLine(GraphicsContext gc, Line line) {
        gc.setStroke(line.color);
        gc.setLineWidth(6.0);
        gc.strokeLine(line.x1, line.y1, line.x2, line.y2);
    }

    private static void drawDot(GraphicsContext gc, Dot dot) {
        gc.setFill(dot.color);
        gc.fillOval(dot.x() - 5, dot.y() - 5, 10, 10);
    }

    private static void drawLabel(GraphicsContext gc, Label label) {
        gc.setFont(Font.font(32));

        gc.setFill(label.color);
        gc.fillText(label.label, label.x, label.y + 4);
    }

    private record Line(double x1, double y1, double x2, double y2, Color color) {
    }

    private record Dot(double x, double y, Color color) {
    }

    @AllArgsConstructor
    private static class Label {
        private String label;
        private double x;
        private double y;
        private Color color;
    }

    private record RouteDraw(List<Line> lines, List<Dot> dots, List<Label> labels, double firstX, double firstY) {

    }
}
