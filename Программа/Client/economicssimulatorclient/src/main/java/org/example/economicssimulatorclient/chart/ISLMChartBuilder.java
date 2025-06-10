package org.example.economicssimulatorclient.chart;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import org.fxyz3d.shapes.primitives.SurfacePlotMesh;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Визуализатор для IS-LM модели, включая 3D surface через FXyz3D.
 */
public class ISLMChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "is_lm":
                node = buildISLMChart(chartData);
                break;
            case "surface":
                node = buildSurfaceChart(chartData);
                break;
            case "timeseries":
                node = buildTimeSeriesChart(chartData);
                break;
            default:
                Label lbl = new Label(org.example.economicssimulatorclient.util.I18n.t("chart.not_impl") + chartKey);
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
     * 1. IS/LM график с равновесием
     */
    private Node buildISLMChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(org.example.economicssimulatorclient.util.I18n.t("chart.income"));
        yAxis.setLabel(org.example.economicssimulatorclient.util.I18n.t("chart.rate"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(org.example.economicssimulatorclient.util.I18n.t("chart.islm.title"));
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #ebebeb;");

        addSeries(chart, chartData, "IS", "IS", "#FF5E3A", 3.0);
        addSeries(chart, chartData, "LM", "LM", "#FFC800", 3.0);

        // Точка равновесия
        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number x = eq.get("income");
            Number y = eq.get("rate");
            if (x != null && y != null) {
                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                eqSeries.setName("Равновесие");
                XYChart.Data<Number, Number> d = new XYChart.Data<>(x, y);
                eqSeries.getData().add(d);
                chart.getData().add(eqSeries);
                // Стилизация точки равновесия (красный кружок)
                d.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-background-color: #37ff37, white; -fx-background-radius: 8px;");
                    }
                });
            }
        }
        return chart;
    }

    /**
     * 2. 3D поверхность — эмуляция как серия 2D-кривых (например, фиксированная ставка или доход)
     * chartData: "surface": List<List<Map<String, Number>>>
     *      каждая List<Map<>> — линия по одному из параметров (например, для разных i)
     */
    private Node buildSurfaceChart(Map<String, Object> chartData) {
        // chartData — это {"slices": [ … ]}, значит:
        @SuppressWarnings("unchecked")
        List<List<Map<String, Number>>> slices =
                (List<List<Map<String, Number>>>) chartData.get("slices");

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(org.example.economicssimulatorclient.util.I18n.t("chart.income"));
        yAxis.setLabel(org.example.economicssimulatorclient.util.I18n.t("chart.rate"));


        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(org.example.economicssimulatorclient.util.I18n.t("chart.islm.surface"));
        chart.setAnimated(false);

        if (slices != null) {
            int idx = 0;
            for (List<Map<String, Number>> slice : slices) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(org.example.economicssimulatorclient.util.I18n.t("chart.slice") + " " + (++idx));
                for (Map<String, Number> pt : slice) {
                    Number income = pt.get("income");
                    Number rate   = pt.get("rate");
                    if (income != null && rate != null) {
                        series.getData().add(new XYChart.Data<>(income, rate));
                    }
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }


    /**
     * 3. Временные ряды — динамика равновесия при изменении политики
     */
    private Node buildTimeSeriesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(org.example.economicssimulatorclient.util.I18n.t("chart.time"));
        yAxis.setLabel(org.example.economicssimulatorclient.util.I18n.t("chart.value"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(org.example.economicssimulatorclient.util.I18n.t("chart.islm.timeseries"));
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #ebebeb;");

        // Временной ряд дохода
        if (chartData.containsKey("policy")) {
            List<Map<String, Number>> seriesPts = (List<Map<String, Number>>) chartData.get("policy");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(org.example.economicssimulatorclient.util.I18n.t("chart.income"));
            for (Map<String, Number> pt : seriesPts) {
                Number t = pt.get("time");
                Number y = pt.get("income");
                if (t != null && y != null)
                    series.getData().add(new XYChart.Data<>(t, y));
            }
            chart.getData().add(series);
        }

        // Временной ряд ставки
        if (chartData.containsKey("rate")) {
            List<Map<String, Number>> seriesPts = (List<Map<String, Number>>) chartData.get("rate");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(org.example.economicssimulatorclient.util.I18n.t("chart.rate"));
            for (Map<String, Number> pt : seriesPts) {
                Number t = pt.get("time");
                Number r = pt.get("rate");
                if (t != null && r != null)
                    series.getData().add(new XYChart.Data<>(t, r));
            }
            chart.getData().add(series);
        }
        return chart;
    }

    // Вспомогательная функция с цветом/толщиной
    private void addSeries(LineChart<Number, Number> chart, Map<String, Object> data, String key, String name, String color, double width) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            for (Map<String, Number> pt : pts) {
                Number x = pt.get("income");
                Number y = pt.get("rate");
                if (x != null && y != null)
                    series.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(series);

            // Стилизуем линию после отрисовки
            series.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(String.format("-fx-stroke: %s; -fx-stroke-width: %.1f;", color, width));
                }
            });
        }
    }
}
