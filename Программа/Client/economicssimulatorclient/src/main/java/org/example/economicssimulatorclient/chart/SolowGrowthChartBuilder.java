package org.example.economicssimulatorclient.chart;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.economicssimulatorclient.util.I18n;

import java.util.List;
import java.util.Map;

/**
 * Построитель графиков неоклассической модели роста Солоу.
 * Позволяет выводить траектории, фазовый портрет и сравнительную статическую.
 */
public class SolowGrowthChartBuilder implements ChartDrawer {

    /**
     * Создаёт диаграмму модели Солоу.
     *
     * @param chartKey  "trajectories", "phase" или "statics"
     * @param chartData параметры для построения
     * @return узел JavaFX с визуализацией
     */
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

    private Node buildTrajectoriesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.time"));
        yAxis.setLabel(I18n.t("chart.value"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.solow.trajectories.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("capital")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("capital");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.capital"));
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
            series.setName(I18n.t("chart.output"));
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

    private Node buildPhaseDiagram(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.capital"));
        yAxis.setLabel(I18n.t("chart.capital_change"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.solow.phase.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("phase")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("phase");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.capital_change") + " / K");
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

    private Node buildComparativeStaticsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.time"));
        yAxis.setLabel(I18n.t("chart.output"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.solow.statics.title"));
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
