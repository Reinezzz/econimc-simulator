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
 * Построитель графиков для модели CAPM.
 * Поддерживает линию рынка ценных бумаг, эффективную границу и декомпозицию доходности.
 */
public class CAPMChartBuilder implements ChartDrawer {

    /**
     * Создаёт график согласно ключу модели CAPM.
     *
     * @param chartKey  "sml", "efficient_frontier" или "decomposition"
     * @param chartData входные данные для построения
     * @return узел JavaFX с нужным графиком
     */
    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "sml":
                node = buildSMLChart(chartData);
                break;
            case "efficient_frontier":
                node = buildEfficientFrontierChart(chartData);
                break;
            case "decomposition":
                node = buildDecompositionChart(chartData);
                break;
            default:
                Label lbl = new Label(I18n.t("chart.not_impl") + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                StackPane errorPane = new StackPane(lbl);
                errorPane.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #fff;");
                errorPane.setPadding(new Insets(16));
                return errorPane;
        }
        if (node instanceof LineChart)
            styleChart((LineChart<?, ?>) node);
        if (node instanceof ScatterChart)
            styleChart((ScatterChart<?, ?>) node);
        return node;
    }

    private void styleChart(ScatterChart<?,?> chart) {
        chart.setPrefWidth(760);
        chart.setPrefHeight(420);
        chart.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
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

    private Node buildSMLChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.beta"));
        yAxis.setLabel(I18n.t("chart.return"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.capm.sml.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("sml")) {
            List<Map<String, Number>> sml = (List<Map<String, Number>>) chartData.get("sml");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("SML");
            for (Map<String, Number> pt : sml) {
                Number beta = pt.get("risk");
                Number ret = pt.get("return");
                if (beta != null && ret != null) {
                    series.getData().add(new XYChart.Data<>(beta, ret));
                }
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private Node buildEfficientFrontierChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.beta"));
        yAxis.setLabel(I18n.t("chart.return"));

        ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.capm.efficient_frontier.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("portfolios")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("portfolios");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.portfolios"));
            for (Map<String, Number> pt : pts) {
                Number sigma = pt.get("risk");
                Number ret = pt.get("return");
                if (sigma != null && ret != null) {
                    series.getData().add(new XYChart.Data<>(sigma, ret));
                }
            }
            chart.getData().add(series);
        }
        if (chartData.containsKey("frontier")) {
            List<Map<String, Number>> frontier = (List<Map<String, Number>>) chartData.get("frontier");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.efficient_frontier_line"));
            for (Map<String, Number> pt : frontier) {
                Number sigma = pt.get("risk");
                Number ret = pt.get("return");
                if (sigma != null && ret != null) {
                    series.getData().add(new XYChart.Data<>(sigma, ret));
                }
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private Node buildDecompositionChart(Map<String, Object> chartData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.category"));
        yAxis.setLabel(I18n.t("chart.revenue"));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.capm.decomposition.title"));
        chart.setAnimated(false);

        XYChart.Series<String, Number> alphaSeries = new XYChart.Series<>();
        alphaSeries.setName("Alpha");
        XYChart.Series<String, Number> betaSeries = new XYChart.Series<>();
        betaSeries.setName("Beta");

        if (chartData.containsKey("decomposition")) {
            List<Map<String, Object>> decomposition = (List<Map<String, Object>>) chartData.get("decomposition");
            for (Map<String, Object> pt : decomposition) {
                String label = String.valueOf(pt.get("label"));
                Number alpha = pt.containsKey("alpha") ? (Number) pt.get("alpha") : null;
                Number beta = pt.containsKey("beta") ? (Number) pt.get("beta") : null;
                if (alpha != null) alphaSeries.getData().add(new XYChart.Data<>(label, alpha));
                if (beta != null) betaSeries.getData().add(new XYChart.Data<>(label, beta));
            }
        }
        chart.getData().addAll(alphaSeries, betaSeries);
        return chart;
    }
}
