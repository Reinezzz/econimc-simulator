package org.example.economicssimulatorclient.chart;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для Модели спроса и предложения.
 */
public class DemandSupplyChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        switch (chartKey) {
            case "supply_demand":
                return buildSupplyDemandChart(chartData);
            case "surplus_area":
                return buildSurplusAreaChart(chartData);
            case "shift_animation":
                return buildShiftAnimation(chartData);
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
    }

    /**
     * 1. Линейный график - кривые спроса и предложения с точкой равновесия.
     * chartData: "demand": List<Map<String, Number>> (x="quantity", y="price")
     *            "supply": List<Map<String, Number>> (x="quantity", y="price")
     *            "equilibrium": Map<String, Number> ("quantity", "price")
     */
    private Node buildSupplyDemandChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Количество");
        yAxis.setLabel("Цена");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Спрос и предложение");
        chart.setAnimated(false);

        addSeries(chart, chartData, "demand", "Спрос");
        addSeries(chart, chartData, "supply", "Предложение");

        // Точка равновесия
        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number q = eq.get("quantity");
            Number p = eq.get("price");
            if (q != null && p != null) {
                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                eqSeries.setName("Равновесие");
                eqSeries.getData().add(new XYChart.Data<>(q, p));
                chart.getData().add(eqSeries);
            }
        }
        return chart;
    }

    private Node buildSurplusAreaChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Количество");
        yAxis.setLabel("Цена");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Излишки потребителя и производителя");
        chart.setAnimated(false);

        addSeries(chart, chartData, "demand", "Спрос");
        addSeries(chart, chartData, "supply", "Предложение");

        // Точка равновесия
        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number q = eq.get("quantity");
            Number p = eq.get("price");
            if (q != null && p != null) {
                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                eqSeries.setName("Равновесие");
                eqSeries.getData().add(new XYChart.Data<>(q, p));
                chart.getData().add(eqSeries);
            }
        }

        // Создаем polygon overlay после layout’а chart
        StackPane root = new StackPane(chart);

        chart.layout(); // важен первый layout для получения displayPosition
        // Overlay области:
        if (chartData.containsKey("consumer_surplus_area")) {
            List<Map<String, Number>> areaPts = (List<Map<String, Number>>) chartData.get("consumer_surplus_area");
            Polygon poly = buildAreaOverlay(chart, xAxis, yAxis, areaPts, Color.rgb(124, 210, 255, 0.4));
            root.getChildren().add(poly);
        }
        if (chartData.containsKey("producer_surplus_area")) {
            List<Map<String, Number>> areaPts = (List<Map<String, Number>>) chartData.get("producer_surplus_area");
            Polygon poly = buildAreaOverlay(chart, xAxis, yAxis, areaPts, Color.rgb(206, 255, 178, 0.4));
            root.getChildren().add(poly);
        }

        // Если нужно, навесь listener для динамической подгонки overlay при изменении размера chart

        return root;
    }

    private Polygon buildAreaOverlay(LineChart<Number, Number> chart,
                                     NumberAxis xAxis, NumberAxis yAxis,
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

        // (Опционально: навесить listener на resize/axis, чтобы overlay корректировался при zoom/pan)
        return poly;
    }

    /**
     * 3. Анимация сдвига кривых — как изменения параметров влияют на равновесие.
     * chartData:
     *   - "demand_shifts": List<List<Map<String, Number>>>
     *   - "supply_shifts": List<List<Map<String, Number>>>
     *   - "equilibriums": List<Map<String, Number>>
     */
    private Node buildShiftAnimation(Map<String, Object> chartData) {
        // Всё делаем поверх LineChart, шаги — по таймеру
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Количество");
        yAxis.setLabel("Цена");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Анимация сдвигов кривых");
        chart.setAnimated(false);

        List<List<Map<String, Number>>> demandShifts = (List<List<Map<String, Number>>>) chartData.get("demand_shifts");
        List<List<Map<String, Number>>> supplyShifts = (List<List<Map<String, Number>>>) chartData.get("supply_shifts");
        List<Map<String, Number>> equilibriums = (List<Map<String, Number>>) chartData.get("equilibriums");

        if (demandShifts == null || supplyShifts == null) {
            Label lbl = new Label("Недостаточно данных для анимации сдвигов.");
            return new StackPane(lbl);
        }
        Timeline timeline = new Timeline();
        int steps = Math.min(demandShifts.size(), supplyShifts.size());
        for (int i = 0; i < steps; i++) {
            final int idx = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i),
                    e -> {
                        chart.getData().clear();
                        addSeries(chart, demandShifts.get(idx), "Спрос");
                        addSeries(chart, supplyShifts.get(idx), "Предложение");
                        // Точка равновесия на этом шаге
                        if (equilibriums != null && idx < equilibriums.size()) {
                            Map<String, Number> eq = equilibriums.get(idx);
                            Number q = eq.get("quantity");
                            Number p = eq.get("price");
                            if (q != null && p != null) {
                                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                                eqSeries.setName("Равновесие");
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

    // Вспомогательные методы

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

    private Polygon makeAreaPolygon(LineChart<Number, Number> chart,
                                    NumberAxis xAxis, NumberAxis yAxis,
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
}
