package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.example.economicssimulatorclient.util.I18n;

import java.util.List;
import java.util.Map;

public class CompetitionChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "profit_curves":
                node = buildProfitCurvesChart(chartData);
                if (node instanceof LineChart<?, ?> chart) {
                    chart.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
                    chart.setLegendSide(javafx.geometry.Side.BOTTOM);
                    chart.setLegendVisible(true);
                    chart.setHorizontalGridLinesVisible(true);
                    chart.setVerticalGridLinesVisible(true);
                    chart.lookupAll(".chart-plot-background").forEach(bg ->
                            bg.setStyle("-fx-background-color: white;"));
                    chart.lookupAll(".chart-horizontal-grid-lines, .chart-vertical-grid-lines")
                            .forEach(grid -> grid.setStyle("-fx-stroke: #ececec;"));
                    chart.setPadding(javafx.geometry.Insets.EMPTY);
                    for (Object seriesObj : chart.getData()) {
                        XYChart.Series<?, ?> series = (XYChart.Series<?, ?>) seriesObj;
                        for (Object dataObj : series.getData()) {
                            XYChart.Data<?, ?> data = (XYChart.Data<?, ?>) dataObj;
                            Node symbol = data.getNode();
                            if (symbol != null) symbol.setVisible(false);
                        }
                    }
                    Node legend = chart.lookup(".chart-legend");
                    if (legend != null) legend.setStyle("-fx-background-color: white;");
                    chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
                break;
            case "comparison_hist":
                node = buildComparisonHistogram(chartData);
                if (node instanceof BarChart<?, ?> chart) {
                    chart.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
                    chart.setLegendSide(javafx.geometry.Side.BOTTOM);
                    chart.setLegendVisible(true);
                    chart.setHorizontalGridLinesVisible(true);
                    chart.setVerticalGridLinesVisible(true);
                    chart.lookupAll(".chart-plot-background").forEach(bg ->
                            bg.setStyle("-fx-background-color: white;"));
                    chart.lookupAll(".chart-horizontal-grid-lines, .chart-vertical-grid-lines")
                            .forEach(grid -> grid.setStyle("-fx-stroke: #ececec;"));
                    chart.setPadding(javafx.geometry.Insets.EMPTY);
                    Node legend = chart.lookup(".chart-legend");
                    if (legend != null) legend.setStyle("-fx-background-color: white;");
                    chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
                break;
            case "deadweight_area":
                node = buildDeadweightAreaChart(chartData);
                if (node instanceof AreaChart<?, ?> chart) {
                    chart.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
                    chart.setLegendSide(javafx.geometry.Side.BOTTOM);
                    chart.setLegendVisible(true);
                    chart.setHorizontalGridLinesVisible(true);
                    chart.setVerticalGridLinesVisible(true);
                    chart.lookupAll(".chart-plot-background").forEach(bg ->
                            bg.setStyle("-fx-background-color: white;"));
                    chart.lookupAll(".chart-horizontal-grid-lines, .chart-vertical-grid-lines")
                            .forEach(grid -> grid.setStyle("-fx-stroke: #ececec;"));
                    chart.setPadding(javafx.geometry.Insets.EMPTY);
                    Node legend = chart.lookup(".chart-legend");
                    if (legend != null) legend.setStyle("-fx-background-color: white;");
                    chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    for (Object seriesObj : chart.getData()) {
                        XYChart.Series<?, ?> series = (XYChart.Series<?, ?>) seriesObj;
                        for (Object dataObj : series.getData()) {
                            XYChart.Data<?, ?> data = (XYChart.Data<?, ?>) dataObj;
                            Node symbol = data.getNode();
                            if (symbol != null) symbol.setVisible(false);
                        }
                    }
                }
                break;
            default:
                Label lbl = new Label(I18n.t("chart.not_impl") + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                node = new StackPane(lbl);
        }
        if (node instanceof Region region) {
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        return node;
    }

    private Node buildProfitCurvesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.quantity"));
        yAxis.setLabel(I18n.t("chart.profit"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.competition.profit_curves.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("competition")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("competition");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.perfect_competition"));
            for (Map<String, Number> pt : pts) {
                Number quantity = pt.get("quantity");
                Number profit = pt.get("profit");
                if (quantity != null && profit != null) {
                    series.getData().add(new XYChart.Data<>(quantity, profit));
                }
            }
            chart.getData().add(series);
        }

        if (chartData.containsKey("monopoly")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("monopoly");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.monopoly"));
            for (Map<String, Number> pt : pts) {
                Number quantity = pt.get("quantity");
                Number profit = pt.get("profit");
                if (quantity != null && profit != null) {
                    series.getData().add(new XYChart.Data<>(quantity, profit));
                }
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private Node buildComparisonHistogram(Map<String, Object> chartData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.category"));
        yAxis.setLabel(I18n.t("chart.value"));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.competition.comparison_hist.title"));
        chart.setAnimated(false);

        List<String> categories = (List<String>) chartData.get("categories");
        List<Number> compValues = (List<Number>) chartData.get("competition");
        List<Number> monoValues = (List<Number>) chartData.get("monopoly");

        XYChart.Series<String, Number> compSeries = new XYChart.Series<>();
        compSeries.setName(I18n.t("chart.perfect_competition"));
        XYChart.Series<String, Number> monoSeries = new XYChart.Series<>();
        monoSeries.setName(I18n.t("chart.monopoly"));

        if (categories != null && compValues != null && monoValues != null) {
            for (int i = 0; i < categories.size(); i++) {
                String label = categories.get(i);
                if (i < compValues.size())
                    compSeries.getData().add(new XYChart.Data<>(label, compValues.get(i)));
                if (i < monoValues.size())
                    monoSeries.getData().add(new XYChart.Data<>(label, monoValues.get(i)));
            }
        }
        chart.getData().addAll(compSeries, monoSeries);
        return chart;
    }

    private Node buildDeadweightAreaChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.quantity"));
        yAxis.setLabel(I18n.t("chart.price"));

        AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.competition.deadweight_area.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("demand")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("demand");
            XYChart.Series<Number, Number> demandSeries = new XYChart.Series<>();
            demandSeries.setName(I18n.t("chart.demand"));
            for (Map<String, Number> pt : pts) {
                Number quantity = pt.get("quantity");
                Number price = pt.get("price");
                if (quantity != null && price != null) {
                    demandSeries.getData().add(new XYChart.Data<>(quantity, price));
                }
            }
            chart.getData().add(demandSeries);
        }
        if (chartData.containsKey("supply")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("supply");
            XYChart.Series<Number, Number> supplySeries = new XYChart.Series<>();
            supplySeries.setName(I18n.t("chart.supply"));
            for (Map<String, Number> pt : pts) {
                Number quantity = pt.get("quantity");
                Number price = pt.get("price");
                if (quantity != null && price != null) {
                    supplySeries.getData().add(new XYChart.Data<>(quantity, price));
                }
            }
            chart.getData().add(supplySeries);
        }

        return chart;
    }
}
