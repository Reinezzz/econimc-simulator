package org.example.economicssimulatorclient.chart;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для модели Блэка-Шоулза.
 */
public class BlackScholesChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "surface":
                node = buildSurfaceChart(chartData);
                break;
            case "decay":
                node = buildTimeDecayChart(chartData);
                break;
            case "greeks":
                node = buildGreeksChart(chartData);
                break;
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                StackPane errorPane = new StackPane(lbl);
                errorPane.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #fff;");
                errorPane.setPadding(new Insets(16));
                return errorPane;
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

    /**
     * 1. "3D поверхность" — цена опциона от цены актива и времени
     * chartData: ключи "surface" : List<List<Map<String, Number>>>
     *  - Каждый List<Map<>>: серия по времени или цене (например, timeSlices)
     */
    private Node buildSurfaceChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Цена актива (S)");
        yAxis.setLabel("Цена опциона (C)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Цена опциона: поверхность (эмуляция 3D)");
        chart.setAnimated(false);

        if (chartData.containsKey("surface")) {
            List<List<Map<String, Number>>> surface = (List<List<Map<String, Number>>>) chartData.get("surface");
            int t = 1;
            for (List<Map<String, Number>> timeSlice : surface) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("t = " + t++);
                for (Map<String, Number> pt : timeSlice) {
                    Number s = pt.get("S");
                    Number c = pt.get("C");
                    if (s != null && c != null) {
                        series.getData().add(new XYChart.Data<>(s, c));
                    }
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }

    /**
     * 2. "Временной распад" — цена опциона по времени
     * chartData: "decay": List<Map<String, Number>> с полями "t" (time), "C" (цена)
     */
    private Node buildTimeDecayChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Время до экспирации (T)");
        yAxis.setLabel("Цена опциона (C)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Временной распад опциона");
        chart.setAnimated(false);

        if (chartData.containsKey("decay")) {
            List<Map<String, Number>> decay = (List<Map<String, Number>>) chartData.get("decay");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Цена опциона");
            for (Map<String, Number> pt : decay) {
                Number t = pt.get("T");
                Number c = pt.get("C");
                if (t != null && c != null) {
                    series.getData().add(new XYChart.Data<>(t, c));
                }
            }
            chart.getData().add(series);
        }
        return chart;
    }

    /**
     * 3. "Греки опциона" — чувствительность к параметрам (дельта, тета, вега, гамма)
     * chartData: "greeks": Map<String, List<Map<String, Number>>> (ключ — грек, значения — точки)
     */
    private Node buildGreeksChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Цена актива (S) или t");
        yAxis.setLabel("Греки");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Греки опциона");
        chart.setAnimated(false);

        if (chartData.containsKey("greeks")) {
            Map<String, List<Map<String, Number>>> greeks = (Map<String, List<Map<String, Number>>>) chartData.get("greeks");
            for (Map.Entry<String, List<Map<String, Number>>> entry : greeks.entrySet()) {
                String greek = entry.getKey();
                List<Map<String, Number>> points = entry.getValue();
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(greek);
                for (Map<String, Number> pt : points) {
                    Number x = pt.containsKey("S") ? pt.get("S") : pt.get("t");
                    Number y = pt.get("value");
                    if (x != null && y != null) {
                        series.getData().add(new XYChart.Data<>(x, y));
                    }
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }
}
