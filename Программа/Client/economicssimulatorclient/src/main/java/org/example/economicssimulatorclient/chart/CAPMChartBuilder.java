package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;

/**
 * Визуализатор для модели CAPM (Capital Asset Pricing Model).
 */
public class CAPMChartBuilder implements ChartDrawer {

    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        switch (chartKey) {
            case "sml":
                return buildSMLChart(chartData);
            case "efficient_frontier":
                return buildEfficientFrontierChart(chartData);
            case "decomposition":
                return buildDecompositionChart(chartData);
            default:
                Label lbl = new Label("График не реализован: " + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }
    }

    /**
     * 1. Линия рынка ценных бумаг (SML)
     * chartData: "sml": List<Map<String, Number>> (x = "risk" (бета), y = "return")
     */
    private Node buildSMLChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Бета (β), риск");
        yAxis.setLabel("Доходность (%)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Линия рынка ценных бумаг (SML)");
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

    /**
     * 2. Scatter plot портфелей — эффективная граница Марковица
     * chartData: "portfolios": List<Map<String, Number>> (x = "risk" (stddev), y = "return")
     * Опционально: "frontier": List<Map<String, Number>> — сама эффективная граница
     */
    private Node buildEfficientFrontierChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Риск (σ)");
        yAxis.setLabel("Доходность (%)");

        ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        chart.setTitle("Эффективная граница портфелей");
        chart.setAnimated(false);

        // Сами портфели
        if (chartData.containsKey("portfolios")) {
            List<Map<String, Number>> pts = (List<Map<String, Number>>) chartData.get("portfolios");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Портфели");
            for (Map<String, Number> pt : pts) {
                Number sigma = pt.get("risk");
                Number ret = pt.get("return");
                if (sigma != null && ret != null) {
                    series.getData().add(new XYChart.Data<>(sigma, ret));
                }
            }
            chart.getData().add(series);
        }
        // Эффективная граница (линия)
        if (chartData.containsKey("frontier")) {
            List<Map<String, Number>> frontier = (List<Map<String, Number>>) chartData.get("frontier");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Эффективная граница");
            for (Map<String, Number> pt : frontier) {
                Number sigma = pt.get("risk");
                Number ret = pt.get("return");
                if (sigma != null && ret != null) {
                    series.getData().add(new XYChart.Data<>(sigma, ret));
                }
            }
            // LineChart бы выглядел красивее, но для scatter добавляем как отдельную серию
            chart.getData().add(series);
        }
        return chart;
    }

    /**
     * 3. Столбчатая диаграмма — декомпозиция доходности (альфа, бета)
     * chartData: "decomposition": List<Map<String, Object>> с "label", "alpha", "beta"
     */
    private Node buildDecompositionChart(Map<String, Object> chartData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Актив");
        yAxis.setLabel("Вклад (%)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Декомпозиция доходности (альфа, бета)");
        chart.setAnimated(false);

        XYChart.Series<String, Number> alphaSeries = new XYChart.Series<>();
        alphaSeries.setName("Альфа");
        XYChart.Series<String, Number> betaSeries = new XYChart.Series<>();
        betaSeries.setName("Бета");

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
