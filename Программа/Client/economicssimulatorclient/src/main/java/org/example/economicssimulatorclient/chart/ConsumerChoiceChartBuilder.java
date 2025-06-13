package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.economicssimulatorclient.util.I18n;

import java.util.List;
import java.util.Map;

/**
 * Построитель графиков теории потребительского выбора.
 * Используется для отображения кривых безразличия, карты оптимума и эффекта дохода/замещения.
 */
public class ConsumerChoiceChartBuilder implements ChartDrawer {

    /**
     * Формирует график потребительского выбора.
     *
     * @param chartKey  "indifference_curves", "optimum_map" или "income_substitution"
     * @param chartData входные данные для построения
     * @return визуальный узел JavaFX
     */
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
                Label lbl = new Label(I18n.t("chart.not_impl") + chartKey);
                lbl.setStyle("-fx-text-fill: red;");
                return new StackPane(lbl);
        }

        if (node instanceof Chart) {
            Chart chart = (Chart) node;
            chart.setStyle("-fx-background-color: white;");

            chart.setMinSize(0, 0);
            chart.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
            chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            chart.lookupAll(".chart-plot-background")
                    .forEach(bg -> bg.setStyle("-fx-background-color: white;"));

            chart.lookupAll(".chart-vertical-grid-lines, .chart-horizontal-grid-lines")
                    .forEach(grid -> grid.setStyle("-fx-stroke: #e6e6e6;"));
            chart.lookupAll(".chart-horizontal-zero-line, .chart-vertical-zero-line")
                    .forEach(zero -> zero.setStyle("-fx-stroke: #bfbfbf; -fx-stroke-width: 1px;"));

            chart.lookupAll(".chart-legend")
                    .forEach(legend -> legend.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px;"));

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

    private Node buildIndifferenceCurvesChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.good_x"));
        yAxis.setLabel(I18n.t("chart.good_y"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.consumer.indifference_curves.title"));
        chart.setAnimated(false);

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

        if (chartData.containsKey("budget")) {
            List<Map<String, Number>> budget = (List<Map<String, Number>>) chartData.get("budget");
            XYChart.Series<Number, Number> budgetSeries = new XYChart.Series<>();
            budgetSeries.setName(I18n.t("chart.budget_line"));
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

    private Node buildOptimumMapChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.good_x"));
        yAxis.setLabel(I18n.t("chart.good_y"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.consumer.optimum_map.title"));
        chart.setAnimated(false);

        if (chartData.containsKey("indifference_curve")) {
            List<Map<String, Number>> curve = (List<Map<String, Number>>) chartData.get("indifference_curve");
            XYChart.Series<Number, Number> curveSeries = new XYChart.Series<>();
            curveSeries.setName(I18n.t("chart.indifference_curves"));
            for (Map<String, Number> pt : curve) {
                Number x = pt.get("quantity");
                Number y = pt.get("price");
                if (x != null && y != null)
                    curveSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(curveSeries);
        }
        if (chartData.containsKey("budget")) {
            List<Map<String, Number>> budget = (List<Map<String, Number>>) chartData.get("budget");
            XYChart.Series<Number, Number> budgetSeries = new XYChart.Series<>();
            budgetSeries.setName(I18n.t("chart.budget_line"));
            for (Map<String, Number> pt : budget) {
                Number x = pt.get("quantity");
                Number y = pt.get("price");
                if (x != null && y != null)
                    budgetSeries.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(budgetSeries);
        }
        if (chartData.containsKey("optimum")) {
            Map<String, Number> pt = (Map<String, Number>) chartData.get("optimum");
            if (pt.get("x") != null && pt.get("y") != null) {
                XYChart.Series<Number, Number> optimumSeries = new XYChart.Series<>();
                optimumSeries.setName(I18n.t("chart.optimum"));
                optimumSeries.getData().add(new XYChart.Data<>(pt.get("x"), pt.get("y")));
                chart.getData().add(optimumSeries);
            }
        }
        return chart;
    }

    private Node buildIncomeSubstitutionChart(Map<String, Object> chartData) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(I18n.t("chart.good_x"));
        yAxis.setLabel(I18n.t("chart.good_y"));

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(I18n.t("chart.consumer.income_substitution.title"));
        chart.setAnimated(false);

        addSeries(chart, chartData, "before", I18n.t("chart.before"));
        addSeries(chart, chartData, "after", I18n.t("chart.after"));
        addSeries(chart, chartData, "substitution", I18n.t("chart.substitution"));
        addSeries(chart, chartData, "income", I18n.t("chart.income_effect"));

        return chart;
    }

    private void addSeries(LineChart<Number, Number> chart, Map<String, Object> data, String key, String name) {
        if (data.containsKey(key)) {
            List<Map<String, Number>> points = (List<Map<String, Number>>) data.get(key);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            for (Map<String, Number> pt : points) {
                Number x = pt.containsKey("x") ? pt.get("x") : pt.get("quantity");
                Number y = pt.containsKey("y") ? pt.get("y") : pt.get("price");
                if (x != null && y != null)
                    series.getData().add(new XYChart.Data<>(x, y));
            }
            chart.getData().add(series);
        }
    }

}
