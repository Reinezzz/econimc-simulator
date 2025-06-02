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
 * Визуализатор для IS-LM модели.
 */
public class ISLMChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        switch (chartKey) {
            case "is_lm":
                return buildISLMChart(chartData);
            case "surface":
                return buildSurfaceChart(chartData);
            case "timeseries":
                return buildTimeSeriesChart(chartData);
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
    }

    /**
     * 1. Двухосевой график — пересечение кривых IS и LM
     * chartData: "IS": List<Map<String, Number>> (x="income", y="rate")
     *            "LM": List<Map<String, Number>> (x="income", y="rate")
     *            "equilibrium": Map<String, Number> (x="income", y="rate")
     */
    private Node buildISLMChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Доход (Y)");
        yAxis.setLabel("Ставка (i)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Модель IS-LM: Кривые и равновесие");
        chart.setAnimated(false);

        addSeries(chart, chartData, "IS", "IS");
        addSeries(chart, chartData, "LM", "LM");

        // Точка равновесия
        if (chartData.containsKey("equilibrium")) {
            Map<String, Number> eq = (Map<String, Number>) chartData.get("equilibrium");
            Number x = eq.get("income");
            Number y = eq.get("rate");
            if (x != null && y != null) {
                XYChart.Series<Number, Number> eqSeries = new XYChart.Series<>();
                eqSeries.setName("Равновесие");
                eqSeries.getData().add(new XYChart.Data<>(x, y));
                chart.getData().add(eqSeries);
            }
        }
        return chart;
    }

    /**
     * 2. 3D поверхность — эмуляция как серия 2D-кривых (например, фиксированная ставка или доход)
     * chartData: "surface": List<List<Map<String, Number>>>
     *      каждая List<Map<>> — линия по одному из параметров (например, для разных i)
     */
    private Node buildSurfaceChart(Map<String, Object> chartData) {
        // chartData — это {"slices": [ … ]}, значит:
        @SuppressWarnings("unchecked")
        List<List<Map<String, Number>>> slices =
                (List<List<Map<String, Number>>>) chartData.get("slices");

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Доход (Y)");
        yAxis.setLabel("Ставка (i)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("3D поверхность: доход-ставка");
        chart.setAnimated(false);

        if (slices != null) {
            int idx = 0;
            for (List<Map<String, Number>> slice : slices) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Срез " + (++idx));
                for (Map<String, Number> pt : slice) {
                    Number income = pt.get("income");
                    Number rate   = pt.get("rate");
                    if (income != null && rate != null) {
                        series.getData().add(new XYChart.Data<>(income, rate));
                    }
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }


    /**
     * 3. Временные ряды — динамика равновесия при изменении политики
     * chartData: "policy": List<Map<String, Number>> (x="time", y="income")
     *            "rate": List<Map<String, Number>> (x="time", y="rate")
     */
    private Node buildTimeSeriesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Время");
        yAxis.setLabel("Значение");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Динамика равновесия IS-LM");
        chart.setAnimated(false);

        // Временной ряд дохода
        if (chartData.containsKey("policy")) {
            List<Map<String, Number>> seriesPts = (List<Map<String, Number>>) chartData.get("policy");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Доход");
            for (Map<String, Number> pt : seriesPts) {
                Number t = pt.get("time");
                Number y = pt.get("income");
                if (t != null && y != null)
                    series.getData().add(new XYChart.Data<>(t, y));
            }
            chart.getData().add(series);
        }

        // Временной ряд ставки
        if (chartData.containsKey("rate")) {
            List<Map<String, Number>> seriesPts = (List<Map<String, Number>>) chartData.get("rate");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Ставка");
            for (Map<String, Number> pt : seriesPts) {
                Number t = pt.get("time");
                Number r = pt.get("rate");
                if (t != null && r != null)
                    series.getData().add(new XYChart.Data<>(t, r));
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private void addSeries(LineChart<Number, Number> chart, Map<String, Object> data, String key, String name) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            for (Map<String, Number> pt : pts) {
                Number x = pt.get("income");
                Number y = pt.get("rate");
                if (x != null && y != null)
                    series.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(series);
        }
    }
}
