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
 * Построитель графиков модели оценки опционов по формуле Блэка–Шоулза.
 * Позволяет визуализировать поверхностные зависимости, временной распад и греческие коэффициенты.
 */
public class BlackScholesChartBuilder implements ChartDrawer {

    /**
     * Создаёт один из графиков модели Блэка–Шоулза.
     *
     * @param chartKey  "surface", "decay" или "greeks"
     * @param chartData исходные данные для графика
     * @return узел JavaFX с построенной диаграммой
     */
    @Override
    public Node buildChart(String chartKey, Map<String, Object> chartData) {
        Node node;
        switch (chartKey) {
            case "surface":
                node = buildSurfaceChart(chartData);
                break;
            case "decay":
                node = buildTimeDecayChart(chartData);
                break;
            case "greeks":
                node = buildGreeksChart(chartData);
                break;
            default:
                Label lbl = new Label(I18n.t("chart.not_impl") + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                StackPane errorPane = new StackPane(lbl);
                errorPane.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #fff;");
                errorPane.setPadding(new Insets(16));
                return errorPane;
        }
        if (node instanceof LineChart) {
            styleChart((LineChart<?, ?>) node);
        }

        return node;
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

    private Node buildSurfaceChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.asset_price"));
        yAxis.setLabel(I18n.t("chart.option_price"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.bs.surface.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("surface")) {
            List<List<Map<String, Number>>> surface = (List<List<Map<String, Number>>>) chartData.get("surface");
            int t = 1;
            for (List<Map<String, Number>> timeSlice : surface) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("t = " + t++);
                for (Map<String, Number> pt : timeSlice) {
                    Number s = pt.get("S");
                    Number c = pt.get("C");
                    if (s != null && c != null) {
                        series.getData().add(new XYChart.Data<>(s, c));
                    }
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }

    private Node buildTimeDecayChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.time_to_expiry"));
        yAxis.setLabel(I18n.t("chart.option_price"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.bs.decay.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("decay")) {
            List<Map<String, Number>> decay = (List<Map<String, Number>>) chartData.get("decay");
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(I18n.t("chart.option_price"));
            for (Map<String, Number> pt : decay) {
                Number t = pt.get("T");
                Number c = pt.get("C");
                if (t != null && c != null) {
                    series.getData().add(new XYChart.Data<>(t, c));
                }
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private Node buildGreeksChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.asset_price") + " / t");
        yAxis.setLabel(I18n.t("chart.greeks"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.bs.greeks.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("greeks")) {
            Map<String, List<Map<String, Number>>> greeks = (Map<String, List<Map<String, Number>>>) chartData.get("greeks");
            for (Map.Entry<String, List<Map<String, Number>>> entry : greeks.entrySet()) {
                String greek = entry.getKey();
                List<Map<String, Number>> points = entry.getValue();
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(greek);
                for (Map<String, Number> pt : points) {
                    Number x = pt.containsKey("S") ? pt.get("S") : pt.get("t");
                    Number y = pt.get("value");
                    if (x != null && y != null) {
                        series.getData().add(new XYChart.Data<>(x, y));
                    }
                }
                chart.getData().add(series);
            }
        }
        return chart;
    }
}
