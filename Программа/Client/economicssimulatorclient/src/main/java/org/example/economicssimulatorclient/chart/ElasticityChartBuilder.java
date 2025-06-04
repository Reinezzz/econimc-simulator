package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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
        Node node;
        switch (chartKey) {
            case "elasticity_curves":
                node = buildElasticityCurves(chartData);
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
                }
                break;
            case "revenue_bars":
                node = buildRevenueBarChart(chartData);
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
                }
                break;
            case "elasticity_heatmap":
                node = buildElasticityHeatmap(chartData);
                StackPane heatmapContainer = new StackPane(node);
                heatmapContainer.setStyle("-fx-background-color: white;");
                heatmapContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                StackPane.setAlignment(node, javafx.geometry.Pos.CENTER);
                node = heatmapContainer;
                break;
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
        return node;
    }


    /**
     * 1. Кривые эластичности (по всем категориям).
     * chartData: ключи "elastic", "inelastic", "seriesN" → List<Map<String, Number>> (x="price", y="quantity")
     */
    private Node buildElasticityCurves(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Цена");
        yAxis.setLabel("Количество");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Сравнение эластичности спроса");
        chart.setAnimated(false);

        // Добавляем все имеющиеся серии (elastic, inelastic, seriesN)
        for (String key : chartData.keySet()) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get(key);
            String name = switch (key) {
                case "elastic" -> "Эластичный спрос";
                case "inelastic" -> "Неэластичный спрос";
                default -> "Категория " + (key.replace("series", ""));
            };
            addSeries(chart, pts, name);
        }
        return chart;
    }

    /**
     * 2. Столбчатая диаграмма — выручка до и после по всем категориям.
     * chartData: "categories": List<String>, "revenue0": List<Number>, "revenue1": List<Number>
     */
    private Node buildRevenueBarChart(Map<String, Object> chartData) {
        List<String> categories = (List<String>) chartData.get("categories");
        List<Number> revenue0 = (List<Number>) chartData.get("revenue0");
        List<Number> revenue1 = (List<Number>) chartData.get("revenue1");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Категория");
        yAxis.setLabel("Общая выручка");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Выручка по категориям");
        chart.setAnimated(false);

        XYChart.Series<String, Number> series0 = new XYChart.Series<>();
        series0.setName("До изменения цены");
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("После изменения цены");

        if (categories != null && revenue0 != null && revenue1 != null) {
            for (int i = 0; i < categories.size(); i++) {
                String label = categories.get(i);
                if (i < revenue0.size()) {
                    Number val0 = revenue0.get(i);
                    if (val0 != null)
                        series0.getData().add(new XYChart.Data<>(label, val0));
                }
                if (i < revenue1.size()) {
                    Number val1 = revenue1.get(i);
                    if (val1 != null)
                        series1.getData().add(new XYChart.Data<>(label, val1));
                }
            }
        }
        chart.getData().addAll(series0, series1);
        return chart;
    }

    /**
     * 3. Тепловая карта — эластичность для разных товарных категорий.
     * chartData: "categories": List<String>, "elasticity": List<Number>
     */
    private Node buildElasticityHeatmap(Map<String, Object> chartData) {
        List<String> categories = (List<String>) chartData.get("categories");
        List<Number> elasticity = (List<Number>) chartData.get("elasticity");

        if (categories == null || elasticity == null) {
            Label lbl = new Label("Нет данных для тепловой карты");
            return new StackPane(lbl);
        }

        int n = Math.min(categories.size(), elasticity.size());
        int cellWidth = 60;
        int cellHeight = 36;

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setStyle("-fx-background-color: white;");
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Ячейки heatmap
        for (int i = 0; i < n; i++) {
            Number value = elasticity.get(i);
            Color color = elasticityColor(value != null ? value.doubleValue() : 0);

            Rectangle rect = new Rectangle(cellWidth, cellHeight, color);
            rect.setArcWidth(6);
            rect.setArcHeight(6);
            rect.setStroke(Color.rgb(210,210,210));
            rect.setStrokeWidth(1);

            Label valLabel = new Label(String.format("%.2f", value.doubleValue()));
            valLabel.setTextFill(Color.BLACK);
            valLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: transparent;");
            StackPane cell = new StackPane(rect, valLabel);
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            grid.add(cell, i, 0);
            GridPane.setHgrow(cell, javafx.scene.layout.Priority.ALWAYS);
        }

        // Подписи категорий снизу
        for (int i = 0; i < n; i++) {
            Label catLabel = new Label(categories.get(i));
            catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #222;");
            catLabel.setWrapText(true);
            catLabel.setMaxWidth(cellWidth);
            catLabel.setAlignment(javafx.geometry.Pos.CENTER);
            grid.add(catLabel, i, 1);
            GridPane.setHgrow(catLabel, javafx.scene.layout.Priority.ALWAYS);
        }

        grid.setHgap(8);
        grid.setVgap(5);

        // Легенда справа
        javafx.scene.layout.VBox legend = new javafx.scene.layout.VBox(0);
        legend.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        int scaleHeight = cellHeight * 2;
        javafx.scene.canvas.Canvas scale = new javafx.scene.canvas.Canvas(20, scaleHeight);
        javafx.scene.canvas.GraphicsContext gc = scale.getGraphicsContext2D();
        for (int y = 0; y < scaleHeight; y++) {
            double frac = 1 - y / (double) (scaleHeight-1);
            Color c = frac < 0.5
                    ? Color.color(0.3, 0.7, 1.0, 0.8 * (0.5-frac) + 0.8 * frac) // синий -> белый
                    : Color.color(1.0, 0.5*(1.5-frac), 0.5*(1.5-frac), 0.8 * frac); // белый -> красный
            gc.setStroke(c);
            gc.strokeLine(0, y, 20, y);
        }
        Label top = new Label("+2");
        Label mid = new Label("0");
        Label bot = new Label("-2");
        top.setStyle("-fx-font-size: 10px;");
        mid.setStyle("-fx-font-size: 10px;");
        bot.setStyle("-fx-font-size: 10px;");
        legend.getChildren().addAll(top, scale, bot);
        legend.setAlignment(javafx.geometry.Pos.CENTER);

        // Всё вместе в HBox, который тоже растягивается
        javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(grid, legend);
        hbox.setSpacing(20);
        hbox.setStyle("-fx-background-color: white;");
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hbox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Внешняя обертка
        StackPane wrapper = new StackPane(hbox);
        wrapper.setStyle("-fx-background-color: white;");
        wrapper.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        StackPane.setAlignment(hbox, javafx.geometry.Pos.CENTER_LEFT);

        return wrapper;
    }



    private Color elasticityColor(double value) {
        value = Math.max(-2, Math.min(2, value));
        if (value < 0) return Color.color(0.3, 0.7, 1.0, 0.8); // синий
        else if (value == 0) return Color.color(1.0, 1.0, 1.0, 0.8); // белый
        else return Color.color(1.0, 0.5, 0.5, 0.8); // красный
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
