package org.example.economicssimulatorclient.chart;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import org.example.economicssimulatorclient.util.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Построитель графиков модели спроса и предложения.
 * Создаёт базовые кривые, области излишков и анимацию сдвига.
 */
public class DemandSupplyChartBuilder implements ChartDrawer {

    /**
     * Создаёт график в зависимости от ключа.
     *
     * @param chartKey  "supply_demand", "surplus_area" или "shift_animation"
     * @param chartData данные модели
     * @return узел JavaFX с построенным графиком
     */
    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "supply_demand":
                node = buildSupplyDemandChart(chartData);
                break;
            case "surplus_area":
                node = buildSurplusAreaChart(chartData);
                break;
            case "shift_animation":
                node = buildShiftAnimation(chartData);
                break;
            default:
                Label lbl = new Label(I18n.t("chart.not_impl") + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                StackPane errorPane = new StackPane(lbl);
                errorPane.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #fff;");
                errorPane.setPadding(new Insets(16));
                return errorPane;
        }

        if (node instanceof StackPane) {
            StackPane pane = (StackPane) node;
            pane.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #fff;");
            pane.setPadding(new Insets(0));
            pane.setPrefSize(750, 500);

            pane.getChildren().stream()
                    .filter(child -> child instanceof LineChart)
                    .forEach(child -> styleChart((LineChart<?, ?>) child));
            return pane;
        }
        if (node instanceof LineChart) {
            styleChart((LineChart<?, ?>) node);
        }

        return node;
    }

    private void styleChart(LineChart<?, ?> chart) {
        chart.setPrefWidth(760);
        chart.setPrefHeight(420);
        chart.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        chart.setCreateSymbols(false);
        chart.applyCss();

        Node plotBackground = chart.lookup(".chart-plot-background");
        if (plotBackground != null)
            plotBackground.setStyle("-fx-background-color: white;");

        Node verticalGrid = chart.lookup(".chart-vertical-grid-lines");
        if (verticalGrid != null)
            verticalGrid.setStyle("-fx-stroke: #ebebeb;");

        Node horizontalGrid = chart.lookup(".chart-horizontal-grid-lines");
        if (horizontalGrid != null)
            horizontalGrid.setStyle("-fx-stroke: #ebebeb;");

        Node legend = chart.lookup(".chart-legend");
        if (legend != null)
            legend.setStyle("-fx-background-color: white; -fx-border-color: #ededed; -fx-border-radius: 8; -fx-padding: 4 12 4 12;");

        chart.setPadding(new Insets(0, 0, 0, 0));
    }

    private Node buildSupplyDemandChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.quantity"));
        yAxis.setLabel(I18n.t("chart.price"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.demand.supply_demand.title"));
        chart.setAnimated(false);

        addSeries(chart, chartData, "demand", I18n.t("chart.demand"));
        addSeries(chart, chartData, "supply", I18n.t("chart.supply"));

        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number q = eq.get("quantity");
            Number p = eq.get("price");
            if (q != null && p != null) {
                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                eqSeries.setName(I18n.t("chart.equilibrium_point"));
                eqSeries.getData().add(new XYChart.Data<>(q, p));
                chart.getData().add(eqSeries);
            }
        }
        return chart;
    }

    private Node buildSurplusAreaChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.quantity"));
        yAxis.setLabel(I18n.t("chart.price"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.demand.surplus_area.title"));
        chart.setAnimated(false);

        addSeries(chart, chartData, "demand", I18n.t("chart.demand"));
        addSeries(chart, chartData, "supply", I18n.t("chart.supply"));

        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number q = eq.get("quantity");
            Number p = eq.get("price");
            if (q != null && p != null) {
                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                eqSeries.setName(I18n.t("chart.equilibrium_point"));
                eqSeries.getData().add(new XYChart.Data<>(q, p));
                chart.getData().add(eqSeries);
            }
        }

        StackPane root = new StackPane(chart);

        List<Polygon> polygons = new ArrayList<>();

        Runnable updatePolygons = () -> {
            root.getChildren().removeAll(polygons);
            polygons.clear();

            if (chartData.containsKey("consumer_surplus_area")) {
                List<Map<String, Number>> areaPts = (List<Map<String, Number>>) chartData.get("consumer_surplus_area");
                Polygon poly = buildAreaOverlay(xAxis, yAxis, areaPts, Color.rgb(124, 210, 255, 0.4));
                polygons.add(poly);
            }
            if (chartData.containsKey("producer_surplus_area")) {
                List<Map<String, Number>> areaPts = (List<Map<String, Number>>) chartData.get("producer_surplus_area");
                Polygon poly = buildAreaOverlay(xAxis, yAxis, areaPts, Color.rgb(206, 255, 178, 0.4));
                polygons.add(poly);
            }
            root.getChildren().addAll(polygons);
        };

        chart.layout();
        Platform.runLater(updatePolygons);

        xAxis.lowerBoundProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(updatePolygons));
        xAxis.upperBoundProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(updatePolygons));
        yAxis.lowerBoundProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(updatePolygons));
        yAxis.upperBoundProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(updatePolygons));

        chart.widthProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(updatePolygons));
        chart.heightProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(updatePolygons));

        return root;
    }

    private Polygon buildAreaOverlay(NumberAxis xAxis, NumberAxis yAxis,
                                     List<Map<String, Number>> pts, Color color) {
        Polygon poly = new Polygon();
        for (Map<String, Number> pt : pts) {
            double x = xAxis.getDisplayPosition(pt.get("quantity"));
            double y = yAxis.getDisplayPosition(pt.get("price"));
            poly.getPoints().addAll(x, y);
        }
        poly.setFill(color);
        poly.setStroke(Color.GRAY);
        poly.setStrokeWidth(1);
        poly.setMouseTransparent(true);
        return poly;
    }

    private Node buildShiftAnimation(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.quantity"));
        yAxis.setLabel(I18n.t("chart.price"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.demand.shift_animation.title"));
        chart.setAnimated(false);

        List<List<Map<String, Number>>> demandShifts = (List<List<Map<String, Number>>>) chartData.get("demand_shifts");
        List<List<Map<String, Number>>> supplyShifts = (List<List<Map<String, Number>>>) chartData.get("supply_shifts");
        List<Map<String, Number>> equilibriums = (List<Map<String, Number>>) chartData.get("equilibriums");

        if (demandShifts == null || supplyShifts == null) {
            Label lbl = new Label(I18n.t("chart.shift.not_enough_data"));
            return new StackPane(lbl);
        }
        Timeline timeline = new Timeline();
        int steps = Math.min(demandShifts.size(), supplyShifts.size());
        for (int i = 0; i < steps; i++) {
            final int idx = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i),
                    e -> {
                        chart.getData().clear();
                        addSeries(chart, demandShifts.get(idx), I18n.t("chart.demand"));
                        addSeries(chart, supplyShifts.get(idx), I18n.t("chart.supply"));
                        // Точка равновесия на этом шаге
                        if (equilibriums != null && idx < equilibriums.size()) {
                            Map<String, Number> eq = equilibriums.get(idx);
                            Number q = eq.get("quantity");
                            Number p = eq.get("price");
                            if (q != null && p != null) {
                                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                                eqSeries.setName(I18n.t("chart.equilibrium_point"));
                                eqSeries.getData().add(new XYChart.Data<>(q, p));
                                chart.getData().add(eqSeries);
                            }
                        }
                    }));
        }
        timeline.setCycleCount(steps);
        timeline.play();

        return chart;
    }

    private void addSeries(LineChart<Number, Number> chart, Map<String, Object> data, String key, String name) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) data.get(key);
            addSeries(chart, pts, name);
        }
    }

    private void addSeries(LineChart<Number, Number> chart, List<Map<String, Number>> pts, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (Map<String, Number> pt : pts) {
            Number q = pt.get("quantity");
            Number p = pt.get("price");
            if (q != null && p != null)
                series.getData().add(new XYChart.Data<>(q, p));
        }
        chart.getData().add(series);
    }

}
