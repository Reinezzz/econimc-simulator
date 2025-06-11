package org.example.economicssimulatorclient.chart;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.economicssimulatorclient.util.I18n;

import java.util.List;
import java.util.Map;

public class ADASChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {

        Node node;
        switch (chartKey) {
            case "equilibrium":
                node = buildEquilibriumChart(chartData);
                break;
            case "shifts":
                node = buildShiftsChart(chartData);
                break;
            case "gaps":
                node = buildGapsChart(chartData);
                break;
            default:
                Label lbl = new Label(I18n.t("chart.not_impl") + chartKey);
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

    private Node buildEquilibriumChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.output"));
        yAxis.setLabel(I18n.t("chart.price_level"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.adas.equilibrium.title"));
        chart.setAnimated(false);

        addSeriesToChart(chart, chartData, "AD", "AD (" + I18n.t("chart.demand") + ")");
        addSeriesToChart(chart, chartData, "SRAS", "SRAS (" + I18n.t("chart.supply") + ")");
        addSeriesToChart(chart, chartData, "LRAS", "LRAS (" + I18n.t("chart.supply") + ")");

        // Точка равновесия
        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number x = eq.get("Y");
            Number y = eq.get("P");
            if (x != null && y != null) {
                XYChart.Series<Number, Number> eqPoint = new XYChart.Series<>();
                eqPoint.setName(I18n.t("chart.equilibrium_point"));
                eqPoint.getData().add(new XYChart.Data<>(x, y));
                chart.getData().add(eqPoint);
            }
        }
        return chart;
    }

    private Node buildShiftsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.output"));
        yAxis.setLabel(I18n.t("chart.price_level"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.adas.shifts.title"));
        chart.setAnimated(false);

        addSeriesToChart(chart, chartData, "AD", "AD (" + I18n.t("chart.before") + ")");
        addSeriesToChart(chart, chartData, "AD2", "AD (" + I18n.t("chart.after") + ")");
        addSeriesToChart(chart, chartData, "SRAS", "SRAS (" + I18n.t("chart.before") + ")");
        addSeriesToChart(chart, chartData, "SRAS2", "SRAS (" + I18n.t("chart.after") + ")");
        addSeriesToChart(chart, chartData, "LRAS", "LRAS");

        return chart;
    }

    private Node buildGapsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.output"));
        yAxis.setLabel(I18n.t("chart.price_level"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.adas.gaps.title"));
        chart.setAnimated(false);

        addSeriesToChart(chart, chartData, "AD", "AD");
        addSeriesToChart(chart, chartData, "SRAS", "SRAS");
        addSeriesToChart(chart, chartData, "LRAS", "LRAS");

        if (chartData.containsKey("potentialY") && chartData.containsKey("actualY")) {
            double potentialY = ((Number) chartData.get("potentialY")).doubleValue();
            double actualY = ((Number) chartData.get("actualY")).doubleValue();
            double minP = yAxis.getLowerBound();
            double maxP = yAxis.getUpperBound();
            Rectangle gap = new Rectangle();
            gap.setX(Math.min(potentialY, actualY));
            gap.setY(minP);
            gap.setWidth(Math.abs(potentialY - actualY));
            gap.setHeight(maxP - minP);
            gap.setFill(Color.rgb(255, 150, 150, 0.3));
            chart.setTitle(chart.getTitle() + String.format(" (Разрыв: %.2f)", actualY - potentialY));
        }
        return chart;
    }

    private void addSeriesToChart(LineChart<Number, Number> chart, Map<String, Object> data, String key, String title) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> points = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(title);
            for (Map<String, Number> point : points) {
                Number x = point.get("x");
                Number y = point.get("y");
                if (x != null && y != null) {
                    series.getData().add(new XYChart.Data<>(x, y));
                }
            }
            chart.getData().add(series);
        }
    }
}
