package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
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
        switch (chartKey) {
            case "indifference_curves":
                return buildIndifferenceCurvesChart(chartData);
            case "optimum_map":
                return buildOptimumMapChart(chartData);
            case "income_substitution":
                return buildIncomeSubstitutionChart(chartData);
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
    }

    /**
     * 1. Кривые безразличия — семейство кривых + бюджетная линия.
     * chartData:
     *   - "indifference_curves": List<List<Map<String, Number>>> (каждая кривая — список точек x,y)
     *   - "budget": List<Map<String, Number>> (x, y)
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
                    Number x = pt.get("x");
                    Number y = pt.get("y");
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
                Number x = pt.get("x");
                Number y = pt.get("y");
                if (x != null && y != null)
                    budgetSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(budgetSeries);
        }
        return chart;
    }

    /**
     * 2. Карта оптимума — точка касания как оптимальный выбор.
     * chartData:
     *   - "indifference_curve": List<Map<String, Number>> (x, y)
     *   - "budget": List<Map<String, Number>> (x, y)
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
                Number x = pt.get("x");
                Number y = pt.get("y");
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
                Number x = pt.get("x");
                Number y = pt.get("y");
                if (x != null && y != null)
                    budgetSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(budgetSeries);
        }
        // Точка оптимума
        if (chartData.containsKey("optimum")) {
            Map<String, Number> pt = (Map<String, Number>) chartData.get("optimum");
            if (pt.get("x") != null && pt.get("y") != null) {
                Circle optimumPoint = new Circle(5, Color.RED);
                optimumPoint.setTranslateX(xAxis.getDisplayPosition(pt.get("x")));
                optimumPoint.setTranslateY(yAxis.getDisplayPosition(pt.get("y")));
                // Более надёжно — отдельной серией:
                XYChart.Series<Number, Number> optimumSeries = new XYChart.Series<>();
                optimumSeries.setName("Оптимум");
                optimumSeries.getData().add(new XYChart.Data<>(pt.get("x"), pt.get("y")));
                chart.getData().add(optimumSeries);
            }
        }
        return chart;
    }

    /**
     * 3. Эффект дохода/замещения — разложение изменения спроса
     * chartData:
     *   - "before": List<Map<String, Number>> (x, y)
     *   - "after": List<Map<String, Number>> (x, y)
     *   - "substitution": List<Map<String, Number>> (x, y)
     *   - "income": List<Map<String, Number>> (x, y)
     */
    private Node buildIncomeSubstitutionChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Товар X");
        yAxis.setLabel("Товар Y");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Эффект дохода и замещения");
        chart.setAnimated(false);

        // До и после изменения цены
        addSeries(chart, chartData, "before", "До изменения");
        addSeries(chart, chartData, "after", "После изменения");
        // Эффекты
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
                Number x = pt.get("x");
                Number y = pt.get("y");
                if (x != null && y != null)
                    series.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(series);
        }
    }
}
