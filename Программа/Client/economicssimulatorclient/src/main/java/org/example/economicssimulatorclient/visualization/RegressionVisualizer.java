package org.example.economicssimulatorclient.visualization;

import org.example.economicssimulatorclient.dto.MathModelDto;
import org.example.economicssimulatorclient.dto.ComputationResultDto;
import javafx.scene.layout.Pane;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.application.Platform;
import org.example.economicssimulatorclient.dto.ModelParameterDto;

import java.util.*;
import java.util.regex.*;

public class RegressionVisualizer implements Visualizer {

    private static final String CHART_SCATTER = "Диаграмма рассеяния";
    private static final String CHART_LINE = "Линия регрессии";

    @Override
    public List<String> getSupportedCharts(MathModelDto model) {
        return List.of(CHART_SCATTER, CHART_LINE);
    }

    @Override
    public void visualize(MathModelDto model, ComputationResultDto result, Pane container, String chartType) {
        container.getChildren().clear();

        // Поиск параметров с данными X и Y
        List<String> varNames = new ArrayList<>();
        List<List<Double>> dataColumns = new ArrayList<>();
        for (var param : model.parameters()) {
            List<Double> col = parseNumberList(param.value());
            if (!col.isEmpty()) {
                varNames.add(param.name());
                dataColumns.add(col);
            }
        }
        if (dataColumns.size() < 2) return; // Нужно хотя бы 2 столбца

        String xName = varNames.get(0);
        String yName = varNames.get(1);
        List<Double> xData = dataColumns.get(0);
        List<Double> yData = dataColumns.get(1);
        int N = Math.min(xData.size(), yData.size());
        if (N < 2) return;

        double minX = Collections.min(xData);
        double maxX = Collections.max(xData);
        double minY = Collections.min(yData);
        double maxY = Collections.max(yData);

        NumberAxis xAxis = new NumberAxis(minX, maxX, (maxX - minX) / 5.0);
        xAxis.setLabel(xName);
        NumberAxis yAxis = new NumberAxis(minY, maxY, (maxY - minY) / 5.0);
        yAxis.setLabel(yName);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        switch (chartType) {
            case CHART_SCATTER -> {
                chart.setTitle("Диаграмма рассеяния: " + yName + " от " + xName);
                XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
                for (int i = 0; i < N; i++) {
                    XYChart.Data<Number, Number> dot = new XYChart.Data<>(xData.get(i), yData.get(i));
                    scatter.getData().add(dot);
                }
                chart.getData().add(scatter);

                Platform.runLater(() -> {
                    for (XYChart.Data<Number, Number> d : scatter.getData()) {
                        d.setNode(new Circle(4, Color.RED));
                    }
                });
            }
            case CHART_LINE -> {
                chart.setTitle("Линия регрессии: " + yName + " от " + xName);

                // 1. Точки (scatter) — для контекста
                XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
                for (int i = 0; i < N; i++) {
                    XYChart.Data<Number, Number> dot = new XYChart.Data<>(xData.get(i), yData.get(i));
                    scatter.getData().add(dot);
                }
                chart.getData().add(scatter);

                Platform.runLater(() -> {
                    for (XYChart.Data<Number, Number> d : scatter.getData()) {
                        d.setNode(new Circle(3, Color.GREY));
                    }
                });

                // 2. Линия регрессии
                double[] coeffs = parseRegressionCoefficients(result, xName, yName);
                if (!Double.isNaN(coeffs[0]) && !Double.isNaN(coeffs[1])) {
                    double a = coeffs[0];
                    double b = coeffs[1];

                    double fx1 = a * minX + b;
                    double fx2 = a * maxX + b;

                    XYChart.Series<Number, Number> regLine = new XYChart.Series<>();
                    regLine.getData().add(new XYChart.Data<>(minX, fx1));
                    regLine.getData().add(new XYChart.Data<>(maxX, fx2));
                    chart.getData().add(regLine);
                }
            }
            default -> {
                // По умолчанию — scatter plot
                chart.setTitle("Диаграмма рассеяния: " + yName + " от " + xName);
                XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
                for (int i = 0; i < N; i++) {
                    XYChart.Data<Number, Number> dot = new XYChart.Data<>(xData.get(i), yData.get(i));
                    scatter.getData().add(dot);
                }
                chart.getData().add(scatter);

                Platform.runLater(() -> {
                    for (XYChart.Data<Number, Number> d : scatter.getData()) {
                        d.setNode(new Circle(4, Color.RED));
                    }
                });
            }
        }

        container.getChildren().add(chart);
    }

    @Override
    public String getDefaultChartType(MathModelDto model) {
        return CHART_SCATTER;
    }

    // ---------- Вспомогательные методы ----------

    private List<Double> parseNumberList(String s) {
        if (s == null) return Collections.emptyList();
        String[] parts = s.split("[,;\\s]+");
        List<Double> list = new ArrayList<>();
        for (String part : parts) {
            try {
                list.add(Double.parseDouble(part.trim()));
            } catch (Exception ignored) {}
        }
        return list;
    }

    // Парсит result типа "y = a*x + b", "y=2.1*x+3.4", "a=2.1; b=3.4", ...
    private double[] parseRegressionCoefficients(ComputationResultDto result, String xName, String yName) {
        if (result == null || result.resultData() == null) return new double[]{Double.NaN, Double.NaN};
        String res = result.resultData();

        // Паттерн для "y = a*x + b"
        Pattern pattern = Pattern.compile(
                yName + "\\s*=\\s*([-+]?\\d*\\.?\\d+)\\s*\\*\\s*" + xName + "\\s*([+-]\\s*\\d*\\.?\\d+)?"
        );
        Matcher m = pattern.matcher(res);
        if (m.find()) {
            double a = Double.parseDouble(m.group(1));
            double b = m.group(2) != null ? Double.parseDouble(m.group(2).replace(" ", "")) : 0.0;
            return new double[]{a, b};
        }
        // Альтернативно ищем "a=..., b=..."
        Pattern pattern2 = Pattern.compile("a\\s*=\\s*([-+]?\\d*\\.?\\d+)[,;\\s]+b\\s*=\\s*([-+]?\\d*\\.?\\d+)");
        Matcher m2 = pattern2.matcher(res);
        if (m2.find()) {
            double a = Double.parseDouble(m2.group(1));
            double b = Double.parseDouble(m2.group(2));
            return new double[]{a, b};
        }
        return new double[]{Double.NaN, Double.NaN};
    }
}
