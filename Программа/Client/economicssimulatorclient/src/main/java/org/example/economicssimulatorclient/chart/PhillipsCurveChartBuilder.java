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
 * Построитель графиков кривой Филлипса.
 * Может отображать точечные диаграммы, временные ряды и коротко-/долгосрочные петли.
 */
public class PhillipsCurveChartBuilder implements ChartDrawer {

    /**
     * Создаёт график кривой Филлипса по ключу.
     *
     * @param chartKey  "scatter", "timeseries" или "loops"
     * @param chartData набор входных данных
     * @return JavaFX-узел с графиком
     */
    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "scatter":
                node = buildScatterChart(chartData);
                break;
            case "timeseries":
                node = buildTimeSeriesChart(chartData);
                break;
            case "loops":
                node = buildLoopsChart(chartData);
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

    private void styleChart(ScatterChart<?, ?> chart) {
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

    private Node buildScatterChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.unemployment"));
        yAxis.setLabel(I18n.t("chart.inflation"));

        ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.phillips.scatter.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("points")) {
            List<Map<String, Number>> points = (List<Map<String, Number>>) chartData.get("points");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.data"));
            for (Map<String, Number> pt : points) {
                Number u = pt.get("unemployment");
                Number inf = pt.get("inflation");
                if (u != null && inf != null)
                    series.getData().add(new XYChart.Data<>(u, inf));
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private Node buildTimeSeriesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.year"));
        yAxis.setLabel(I18n.t("chart.value"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.phillips.timeseries.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("unemployment")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("unemployment");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.unemployment"));
            for (Map<String, Number> pt : pts) {
                Number year = pt.get("year");
                Number value = pt.get("unemployment");
                if (year != null && value != null)
                    series.getData().add(new XYChart.Data<>(year, value));
            }
            chart.getData().add(series);
        }
        if (chartData.containsKey("inflation")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("inflation");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.inflation"));
            for (Map<String, Number> pt : pts) {
                Number year = pt.get("year");
                Number value = pt.get("inflation");
                if (year != null && value != null)
                    series.getData().add(new XYChart.Data<>(year, value));
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private Node buildLoopsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.unemployment"));
        yAxis.setLabel(I18n.t("chart.inflation"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.phillips.loops.title"));
        chart.setAnimated(false);

        addSeries(chart, chartData, "short_run", I18n.t("chart.short_run"));
        addSeries(chart, chartData, "long_run", I18n.t("chart.long_run"));
        return chart;
    }

    private void addSeries(LineChart<Number, Number> chart, Map<String, Object> data, String key, String name) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            for (Map<String, Number> pt : pts) {
                Number u = pt.get("unemployment");
                Number inf = pt.get("inflation");
                if (u != null && inf != null)
                    series.getData().add(new XYChart.Data<>(u, inf));
            }
            chart.getData().add(series);
        }
    }
}
