package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для модели эластичности спроса.
 */
public class ElasticityChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        switch (chartKey) {
            case "elasticity_curves":
                return buildElasticityCurves(chartData);
            case "revenue_bars":
                return buildRevenueBarChart(chartData);
            case "elasticity_heatmap":
                return buildElasticityHeatmap(chartData);
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
    }

    /**
     * 1. Кривые разной эластичности — сравнение эластичного/неэластичного спроса.
     * chartData: "elastic": List<Map<String, Number>> (x="price", y="quantity")
     *            "inelastic": List<Map<String, Number>> (x="price", y="quantity")
     */
    private Node buildElasticityCurves(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Цена");
        yAxis.setLabel("Количество");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Сравнение эластичности спроса");
        chart.setAnimated(false);

        addSeries(chart, chartData, "elastic", "Эластичный спрос");
        addSeries(chart, chartData, "inelastic", "Неэластичный спрос");

        return chart;
    }

    /**
     * 2. Столбчатая диаграмма — изменение общей выручки при разных ценах.
     * chartData: "prices": List<Number>
     *            "revenue": List<Number>
     */
    private Node buildRevenueBarChart(Map<String, Object> chartData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Цена");
        yAxis.setLabel("Общая выручка");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Общая выручка при разных ценах");
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Выручка");

        List<Number> prices = (List<Number>) chartData.get("prices");
        List<Number> revenue = (List<Number>) chartData.get("revenue");
        if (prices != null && revenue != null) {
            for (int i = 0; i < Math.min(prices.size(), revenue.size()); i++) {
                String label = String.valueOf(prices.get(i));
                Number value = revenue.get(i);
                if (value != null)
                    series.getData().add(new XYChart.Data<>(label, value));
            }
        }
        chart.getData().add(series);
        return chart;
    }

    /**
     * 3. Тепловая карта — эластичность для разных товарных категорий.
     * chartData: "categories": List<String>
     *            "elasticity": List<Number>
     */
    private Node buildElasticityHeatmap(Map<String, Object> chartData) {
        // JavaFX не имеет готового HeatMap. Эмулируем через GridPane с заливкой Rectangle.
        List<String> categories = (List<String>) chartData.get("categories");
        List<Number> elasticity = (List<Number>) chartData.get("elasticity");

        if (categories == null || elasticity == null) {
            Label lbl = new Label("Нет данных для тепловой карты");
            return new StackPane(lbl);
        }

        int cellWidth = 70;
        int cellHeight = 35;
        int n = Math.min(categories.size(), elasticity.size());

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        for (int i = 0; i < n; i++) {
            String cat = categories.get(i);
            Number value = elasticity.get(i);
            Color color = elasticityColor(value != null ? value.doubleValue() : 0);

            Rectangle rect = new Rectangle(cellWidth, cellHeight, color);
            Label txt = new Label(cat + "\n" + value);
            txt.setTextFill(Color.BLACK);
            txt.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
            StackPane cell = new StackPane(rect, txt);
            grid.add(cell, i, 0);
        }
        grid.setHgap(8);
        grid.setVgap(8);
        return grid;
    }

    // Цветовая шкала: -1 (синий), 0 (белый), 1 (красный)
    private Color elasticityColor(double value) {
        // value ~ [-2, +2], масштабируем
        value = Math.max(-2, Math.min(2, value));
        if (value < 0) return Color.color(0.3, 0.7, 1.0, 0.8); // синий
        else if (value == 0) return Color.color(1.0, 1.0, 1.0, 0.8); // белый
        else return Color.color(1.0, 0.5, 0.5, 0.8); // красный
    }

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
            Number x = pt.get("price");
            Number y = pt.get("quantity");
            if (x != null && y != null)
                series.getData().add(new XYChart.Data<>(x, y));
        }
        chart.getData().add(series);
    }
}
