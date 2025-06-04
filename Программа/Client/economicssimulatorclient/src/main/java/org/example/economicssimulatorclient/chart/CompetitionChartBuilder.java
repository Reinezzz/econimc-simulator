package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для сравнения совершенной конкуренции и монополии.
 */
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
                    // Сетка светло-серая
                    chart.lookupAll(".chart-horizontal-grid-lines, .chart-vertical-grid-lines")
                            .forEach(grid -> grid.setStyle("-fx-stroke: #ececec;"));
                    // Убираем padding
                    chart.setPadding(javafx.geometry.Insets.EMPTY);
                    // Прямые линии, без точек
                    for (Object seriesObj : chart.getData()) {
                        XYChart.Series<?, ?> series = (XYChart.Series<?, ?>) seriesObj;
                        for (Object dataObj : series.getData()) {
                            XYChart.Data<?, ?> data = (XYChart.Data<?, ?>) dataObj;
                            Node symbol = data.getNode();
                            if (symbol != null) symbol.setVisible(false);
                        }
                    }
                    // Легенда на белом
                    Node legend = chart.lookup(".chart-legend");
                    if (legend != null) legend.setStyle("-fx-background-color: white;");
                    // Растяжение графика
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
                    // Легенда на белом
                    Node legend = chart.lookup(".chart-legend");
                    if (legend != null) legend.setStyle("-fx-background-color: white;");
                    chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    // Если внутри AreaChart есть маркеры точек — убрать:
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
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                node = new StackPane(lbl);
        }
        // Гарантированно растягиваем Node на всю доступную область (для StackPane)
        if (node instanceof Region region) {
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        return node;
    }

    /**
     * 1. Сравнительные графики — наложение кривых прибыли для двух структур
     * chartData: "competition": List<Map<String, Number>> (x="quantity", y="profit")
     *            "monopoly": List<Map<String, Number>> (x="quantity", y="profit")
     */
    private Node buildProfitCurvesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Количество");
        yAxis.setLabel("Прибыль");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Кривые прибыли: Конкуренция vs Монополия");
        chart.setAnimated(false);

        if (chartData.containsKey("competition")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("competition");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Совершенная конкуренция");
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
            series.setName("Монополия");
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

    /**
     * 2. Гистограмма — сравнение цен, количеств, прибыли (по группам: конкуренция, монополия)
     * chartData: "categories": List<String> — например ["Цена", "Количество", "Прибыль"]
     *            "competition": List<Number>
     *            "monopoly": List<Number>
     */
    private Node buildComparisonHistogram(Map<String, Object> chartData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Показатель");
        yAxis.setLabel("Значение");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Сравнение: Конкуренция vs Монополия");
        chart.setAnimated(false);

        List<String> categories = (List<String>) chartData.get("categories");
        List<Number> compValues = (List<Number>) chartData.get("competition");
        List<Number> monoValues = (List<Number>) chartData.get("monopoly");

        XYChart.Series<String, Number> compSeries = new XYChart.Series<>();
        compSeries.setName("Совершенная конкуренция");
        XYChart.Series<String, Number> monoSeries = new XYChart.Series<>();
        monoSeries.setName("Монополия");

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

    /**
     * 3. Площадная диаграмма — потери от монопольной власти (deadweight loss)
     * chartData: "demand": List<Map<String, Number>> (x="quantity", y="price")
     *            "supply": List<Map<String, Number>> (x="quantity", y="price")
     *            "monopolyQ": Number (кол-во монополии)
     *            "competitionQ": Number (кол-во конкуренции)
     */
    private Node buildDeadweightAreaChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Количество");
        yAxis.setLabel("Цена");

        AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setTitle("Потери от монопольной власти (Deadweight Loss)");
        chart.setAnimated(false);

        // Кривые спроса и предложения
        if (chartData.containsKey("demand")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("demand");
            XYChart.Series<Number, Number> demandSeries = new XYChart.Series<>();
            demandSeries.setName("Спрос");
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
            supplySeries.setName("Предложение");
            for (Map<String, Number> pt : pts) {
                Number quantity = pt.get("quantity");
                Number price = pt.get("price");
                if (quantity != null && price != null) {
                    supplySeries.getData().add(new XYChart.Data<>(quantity, price));
                }
            }
            chart.getData().add(supplySeries);
        }
        // Можно добавить отдельную подсветку deadweight loss через стилизованный Polygon/Region
        // (Зависит от детализации chartData)

        return chart;
    }
}
