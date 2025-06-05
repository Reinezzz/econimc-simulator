package org.example.economicssimulatorclient.chart;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для модели роста Солоу.
 */
public class SolowGrowthChartBuilder implements ChartDrawer {

    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "trajectories":
                node = buildTrajectoriesChart(chartData);
                break;
            case "phase":
                node = buildPhaseDiagram(chartData);
                break;
            case "statics":
                node = buildComparativeStaticsChart(chartData);
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
     * 1. Экспоненциальный график — траектории роста капитала и выпуска.
     * chartData: "capital": List<Map<String, Number>> ("time", "capital")
     *            "output": List<Map<String, Number>> ("time", "output")
     */
    private Node buildTrajectoriesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Время");
        yAxis.setLabel("Значение");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Рост капитала и выпуска");
        chart.setAnimated(false);

        if (chartData.containsKey("capital")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("capital");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Капитал");
            for (Map<String, Number> pt : pts) {
                Number t = pt.get("time");
                Number k = pt.get("capital");
                if (t != null && k != null)
                    series.getData().add(new XYChart.Data<>(t, k));
            }
            chart.getData().add(series);
        }
        if (chartData.containsKey("output")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("output");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Выпуск");
            for (Map<String, Number> pt : pts) {
                Number t = pt.get("time");
                Number y = pt.get("output");
                if (t != null && y != null)
                    series.getData().add(new XYChart.Data<>(t, y));
            }
            chart.getData().add(series);
        }
        return chart;
    }

    /**
     * 2. Фазовая диаграмма — сходимость к устойчивому состоянию.
     * chartData: "phase": List<Map<String, Number>> ("capital", "capital_change")
     */
    private Node buildPhaseDiagram(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Капитал (K)");
        yAxis.setLabel("Изменение капитала (ΔK)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Фазовая диаграмма (сходимость)");
        chart.setAnimated(false);

        if (chartData.containsKey("phase")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("phase");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("ΔK от K");
            for (Map<String, Number> pt : pts) {
                Number k = pt.get("capital");
                Number dk = pt.get("capital_change");
                if (k != null && dk != null)
                    series.getData().add(new XYChart.Data<>(k, dk));
            }
            chart.getData().add(series);
        }
        return chart;
    }

    /**
     * 3. Сравнительная статика — влияние параметров на долгосрочный рост.
     * chartData: "scenarios": Map<String, List<Map<String, Number>>> (ключ - сценарий, value - List<"time","output">)
     */
    private Node buildComparativeStaticsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Время");
        yAxis.setLabel("Выпуск (Y)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Сравнительная статика: долгосрочный рост");
        chart.setAnimated(false);

        if (chartData.containsKey("scenarios")) {
            Map<String, List<Map<String, Number>>> scenarios = (Map<String, List<Map<String, Number>>>) chartData.get("scenarios");
            for (Map.Entry<String, List<Map<String, Number>>> entry : scenarios.entrySet()) {
                String scenarioName = entry.getKey();
                List<Map<String, Number>> pts = entry.getValue();
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(scenarioName);
                for (Map<String, Number> pt : pts) {
                    Number t = pt.get("time");
                    Number y = pt.get("output");
                    if (t != null && y != null)
                        series.getData().add(new XYChart.Data<>(t, y));
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }
}
