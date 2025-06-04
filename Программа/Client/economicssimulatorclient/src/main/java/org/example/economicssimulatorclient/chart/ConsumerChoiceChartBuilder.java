package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для Теории потребительского выбора.
 */
public class ConsumerChoiceChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "indifference_curves":
                node = buildIndifferenceCurvesChart(chartData);
                break;
            case "optimum_map":
                node = buildOptimumMapChart(chartData);
                break;
            case "income_substitution":
                node = buildIncomeSubstitutionChart(chartData);
                break;
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }

        // Универсальная стилизация для всех графиков LineChart/BarChart
        if (node instanceof Chart) {
            Chart chart = (Chart) node;
            // Белый фон
            chart.setStyle("-fx-background-color: white;");

            // Растягиваем на всю доступную область
            chart.setMinSize(0, 0);
            chart.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
            chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            // Стилизация области построения
            chart.lookupAll(".chart-plot-background")
                    .forEach(bg -> bg.setStyle("-fx-background-color: white;"));

            // Светлая серая сетка
            chart.lookupAll(".chart-vertical-grid-lines, .chart-horizontal-grid-lines")
                    .forEach(grid -> grid.setStyle("-fx-stroke: #e6e6e6;"));
            chart.lookupAll(".chart-horizontal-zero-line, .chart-vertical-zero-line")
                    .forEach(zero -> zero.setStyle("-fx-stroke: #bfbfbf; -fx-stroke-width: 1px;"));

            // Легенда на белом фоне
            chart.lookupAll(".chart-legend")
                    .forEach(legend -> legend.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px;"));

            // Убрать точки для LineChart (только линии)
            if (chart instanceof LineChart) {
                LineChart<?, ?> lineChart = (LineChart<?, ?>) chart;
                for (Object objSeries : lineChart.getData()) {
                    XYChart.Series<?, ?> series = (XYChart.Series<?, ?>) objSeries;
                    for (Object objData : series.getData()) {
                        XYChart.Data<?, ?> data = (XYChart.Data<?, ?>) objData;
                        if (data.getNode() != null)
                            data.getNode().setStyle("-fx-background-color: transparent; -fx-background-radius: 0px;");
                    }
                }
            }

        }
        return node;
    }

    /**
     * Кривые безразличия — семейство кривых + бюджетная линия.
     * chartData:
     *   - "indifference_curves": List<List<Map<String, Number>>> (quantity, price)
     *   - "budget": List<Map<String, Number>> (quantity, price)
     */
    private Node buildIndifferenceCurvesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Товар X");
        yAxis.setLabel("Товар Y");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Кривые безразличия и бюджетная линия");
        chart.setAnimated(false);

        // Семейство кривых безразличия
        if (chartData.containsKey("indifference_curves")) {
            List<List<Map<String, Number>>> curves = (List<List<Map<String, Number>>>) chartData.get("indifference_curves");
            int i = 1;
            for (List<Map<String, Number>> curve : curves) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("U=" + i++);
                for (Map<String, Number> pt : curve) {
                    Number x = pt.get("quantity");
                    Number y = pt.get("price");
                    if (x != null && y != null)
                        series.getData().add(new XYChart.Data<>(x, y));
                }
                chart.getData().add(series);
            }
        }

        // Бюджетная линия
        if (chartData.containsKey("budget")) {
            List<Map<String, Number>> budget = (List<Map<String, Number>>) chartData.get("budget");
            XYChart.Series<Number, Number> budgetSeries = new XYChart.Series<>();
            budgetSeries.setName("Бюджетная линия");
            for (Map<String, Number> pt : budget) {
                Number x = pt.get("quantity");
                Number y = pt.get("price");
                if (x != null && y != null)
                    budgetSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(budgetSeries);
        }
        return chart;
    }

    /**
     * Карта оптимума — точка касания как оптимальный выбор.
     * chartData:
     *   - "indifference_curve": List<Map<String, Number>> (quantity, price)
     *   - "budget": List<Map<String, Number>> (quantity, price)
     *   - "optimum": Map<String, Number> (x, y)
     */
    private Node buildOptimumMapChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Товар X");
        yAxis.setLabel("Товар Y");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Оптимум потребителя");
        chart.setAnimated(false);

        // Кривая безразличия
        if (chartData.containsKey("indifference_curve")) {
            List<Map<String, Number>> curve = (List<Map<String, Number>>) chartData.get("indifference_curve");
            XYChart.Series<Number, Number> curveSeries = new XYChart.Series<>();
            curveSeries.setName("Кривая безразличия");
            for (Map<String, Number> pt : curve) {
                Number x = pt.get("quantity");
                Number y = pt.get("price");
                if (x != null && y != null)
                    curveSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(curveSeries);
        }
        // Бюджетная линия
        if (chartData.containsKey("budget")) {
            List<Map<String, Number>> budget = (List<Map<String, Number>>) chartData.get("budget");
            XYChart.Series<Number, Number> budgetSeries = new XYChart.Series<>();
            budgetSeries.setName("Бюджетная линия");
            for (Map<String, Number> pt : budget) {
                Number x = pt.get("quantity");
                Number y = pt.get("price");
                if (x != null && y != null)
                    budgetSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(budgetSeries);
        }
        // Точка оптимума
        if (chartData.containsKey("optimum")) {
            Map<String, Number> pt = (Map<String, Number>) chartData.get("optimum");
            if (pt.get("x") != null && pt.get("y") != null) {
                XYChart.Series<Number, Number> optimumSeries = new XYChart.Series<>();
                optimumSeries.setName("Оптимум");
                optimumSeries.getData().add(new XYChart.Data<>(pt.get("x"), pt.get("y")));
                chart.getData().add(optimumSeries);
            }
        }
        return chart;
    }

    /**
     * Эффект дохода/замещения — разложение изменения спроса
     * chartData:
     *   - "before": List<Map<String, Number>> (quantity, price)
     *   - "after": List<Map<String, Number>> (quantity, price)
     *   - "substitution": List<Map<String, Number>> (quantity, price)
     *   - "income": List<Map<String, Number>> (quantity, price)
     */
    private Node buildIncomeSubstitutionChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Товар X");
        yAxis.setLabel("Товар Y");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Эффект дохода и замещения");
        chart.setAnimated(false);

        addSeries(chart, chartData, "before", "До изменения");
        addSeries(chart, chartData, "after", "После изменения");
        addSeries(chart, chartData, "substitution", "Замещение");
        addSeries(chart, chartData, "income", "Доход");

        return chart;
    }

    private void addSeries(LineChart<Number, Number> chart, Map<String, Object> data, String key, String name) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> points = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            for (Map<String, Number> pt : points) {
                // Предпочитаем x/y, если есть, иначе quantity/price
                Number x = pt.containsKey("x") ? pt.get("x") : pt.get("quantity");
                Number y = pt.containsKey("y") ? pt.get("y") : pt.get("price");
                if (x != null && y != null)
                    series.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(series);
        }
    }

}
