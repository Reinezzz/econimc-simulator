package org.example.economicssimulatorclient.chart;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для модели AD-AS (Aggregate Demand - Aggregate Supply).
 */
public class ADASChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        switch (chartKey) {
            case "equilibrium":
                return buildEquilibriumChart(chartData);
            case "shifts":
                return buildShiftsChart(chartData);
            case "gaps":
                return buildGapsChart(chartData);
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
    }

    /**
     * 1. Пересечение кривых - равновесие совокупного спроса и предложения.
     * chartData ожидает Map<String, List<Map<String, Number>>>
     * где ключи: "AD", "SRAS", "LRAS", "equilibrium"
     */
    private Node buildEquilibriumChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Y (Выпуск)");
        yAxis.setLabel("P (Цена)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Равновесие AD-AS");
        chart.setAnimated(false);

        addSeriesToChart(chart, chartData, "AD", "AD (Спрос)");
        addSeriesToChart(chart, chartData, "SRAS", "SRAS (Краткосрочное предложение)");
        addSeriesToChart(chart, chartData, "LRAS", "LRAS (Долгосрочное предложение)");

        // Точка равновесия
        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number x = eq.get("Y");
            Number y = eq.get("P");
            if (x != null && y != null) {
                XYChart.Series<Number, Number> eqPoint = new XYChart.Series<>();
                eqPoint.setName("Равновесие");
                eqPoint.getData().add(new XYChart.Data<>(x, y));
                chart.getData().add(eqPoint);
            }
        }
        return chart;
    }

    /**
     * 2. Сдвиги кривых - влияние шоков спроса/предложения.
     * chartData: ключи "AD", "AD2", "SRAS", "SRAS2", "LRAS"
     */
    private Node buildShiftsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Y (Выпуск)");
        yAxis.setLabel("P (Цена)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Сдвиги кривых AD/AS");
        chart.setAnimated(false);

        addSeriesToChart(chart, chartData, "AD", "AD (до)");
        addSeriesToChart(chart, chartData, "AD2", "AD (после)");
        addSeriesToChart(chart, chartData, "SRAS", "SRAS (до)");
        addSeriesToChart(chart, chartData, "SRAS2", "SRAS (после)");
        addSeriesToChart(chart, chartData, "LRAS", "LRAS");

        return chart;
    }

    /**
     * 3. Инфляционные/рецессионные разрывы - отклонения от потенциального ВВП.
     * chartData: ключи "AD", "SRAS", "LRAS", "potentialY", "actualY"
     */
    private Node buildGapsChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Y (Выпуск)");
        yAxis.setLabel("P (Цена)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Инфляционные/рецессионные разрывы");
        chart.setAnimated(false);

        addSeriesToChart(chart, chartData, "AD", "AD");
        addSeriesToChart(chart, chartData, "SRAS", "SRAS");
        addSeriesToChart(chart, chartData, "LRAS", "LRAS");

        // Подсветка разрыва
        if (chartData.containsKey("potentialY") && chartData.containsKey("actualY")) {
            double potentialY = ((Number) chartData.get("potentialY")).doubleValue();
            double actualY = ((Number) chartData.get("actualY")).doubleValue();
            double minP = yAxis.getLowerBound();
            double maxP = yAxis.getUpperBound();
            Rectangle gap = new Rectangle();
            gap.setX(Math.min(potentialY, actualY));
            gap.setY(minP);
            gap.setWidth(Math.abs(potentialY - actualY));
            gap.setHeight(maxP - minP);
            gap.setFill(Color.rgb(255, 150, 150, 0.3));
            // Для простоты — можно использовать Line или Label для обозначения разрыва
            chart.setTitle(chart.getTitle() + String.format(" (Разрыв: %.2f)", actualY - potentialY));
        }
        return chart;
    }

    /**
     * Добавляет линию на график, если есть такие данные.
     */
    private void addSeriesToChart(LineChart<Number, Number> chart, Map<String, Object> data, String key, String title) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> points = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(title);
            for (Map<String, Number> point : points) {
                Number x = point.get("x");
                Number y = point.get("y");
                if (x != null && y != null) {
                    series.getData().add(new XYChart.Data<>(x, y));
                }
            }
            chart.getData().add(series);
        }
    }
}
