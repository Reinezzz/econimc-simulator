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

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.*;

public class AlgebraicEquationVisualizer implements Visualizer {

    // Список поддерживаемых типов графиков
    private static final String CHART_LINE = "График функции";
    private static final String CHART_ROOT = "Отметка корня";

    @Override
    public List<String> getSupportedCharts(MathModelDto model) {
        // Можно анализировать модель, чтобы динамически возвращать варианты
        return List.of(CHART_LINE, CHART_ROOT);
    }

    @Override
    public void visualize(MathModelDto model, ComputationResultDto result, Pane container, String chartType) {
        container.getChildren().clear();
        String formula = model.formula();
        if (formula == null || formula.isBlank()) return;

        Map<String, Double> paramValues = new HashMap<>();
        for (ModelParameterDto param : model.parameters()) {
            try {
                paramValues.put(param.name(), Double.parseDouble(param.value()));
            } catch (Exception ignored) {}
        }

        Set<String> allVars = extractVariables(formula);
        String unknownVar = allVars.stream()
                .filter(var -> !paramValues.containsKey(var))
                .findFirst()
                .orElse(null);
        if (unknownVar == null) return;

        switch (chartType) {
            case CHART_LINE -> showFunctionChart(formula, unknownVar, paramValues, container);
            case CHART_ROOT -> showRootMarker(formula, unknownVar, paramValues, result, container);
            default -> showFunctionChart(formula, unknownVar, paramValues, container); // по умолчанию
        }
    }

    @Override
    public String getDefaultChartType(MathModelDto model) {
        return CHART_LINE;
    }

    // ===== График функции: y = f(x) =====
    private void showFunctionChart(String formula, String unknownVar, Map<String, Double> paramValues, Pane container) {
        NumberAxis xAxis = new NumberAxis(-10, 10, 1);
        xAxis.setLabel(unknownVar);
        NumberAxis yAxis = new NumberAxis(-10, 10, 1);
        yAxis.setLabel("y");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setTitle("График: " + formula);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (double x = -10; x <= 10; x += 0.1) {
            Map<String, Double> vars = new HashMap<>(paramValues);
            vars.put(unknownVar, x);
            try {
                Expression exp = new ExpressionBuilder(formula)
                        .variables(vars.keySet())
                        .build()
                        .setVariables(vars);
                double y = exp.evaluate();
                if (!Double.isNaN(y) && !Double.isInfinite(y))
                    series.getData().add(new XYChart.Data<>(x, y));
            } catch (Exception ignored) {}
        }
        lineChart.getData().add(series);
        container.getChildren().add(lineChart);
    }

    // ===== График с отметкой корня (решения) =====
    private void showRootMarker(String formula, String unknownVar, Map<String, Double> paramValues, ComputationResultDto result, Pane container) {
        NumberAxis xAxis = new NumberAxis(-10, 10, 1);
        xAxis.setLabel(unknownVar);
        NumberAxis yAxis = new NumberAxis(-10, 10, 1);
        yAxis.setLabel("y");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setTitle("Корень уравнения: " + formula);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (double x = -10; x <= 10; x += 0.1) {
            Map<String, Double> vars = new HashMap<>(paramValues);
            vars.put(unknownVar, x);
            try {
                Expression exp = new ExpressionBuilder(formula)
                        .variables(vars.keySet())
                        .build()
                        .setVariables(vars);
                double y = exp.evaluate();
                if (!Double.isNaN(y) && !Double.isInfinite(y))
                    series.getData().add(new XYChart.Data<>(x, y));
            } catch (Exception ignored) {}
        }
        lineChart.getData().add(series);

        // Отметка найденного корня, если есть
        double solvedX = parseValueFromResult(result, unknownVar);
        if (!Double.isNaN(solvedX)) {
            Map<String, Double> vars = new HashMap<>(paramValues);
            vars.put(unknownVar, solvedX);
            double yAtX = Double.NaN;
            try {
                Expression exp = new ExpressionBuilder(formula)
                        .variables(vars.keySet())
                        .build()
                        .setVariables(vars);
                yAtX = exp.evaluate();
            } catch (Exception ignored) {}
            if (!Double.isNaN(yAtX) && !Double.isInfinite(yAtX)) {
                XYChart.Data<Number, Number> marker = new XYChart.Data<>(solvedX, yAtX);
                series.getData().add(marker);
                Platform.runLater(() -> {
                    Circle c = new Circle(6, Color.RED);
                    marker.setNode(c);
                });
            }
        }

        container.getChildren().add(lineChart);
    }

    // ==== Вспомогательные методы ====
    private Set<String> extractVariables(String expr) {
        Set<String> vars = new HashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z_]\\w*\\b").matcher(expr);
        while (matcher.find()) {
            String var = matcher.group();
            if (!isNumeric(var)) vars.add(var);
        }
        return vars;
    }

    private boolean isNumeric(String str) {
        try { Double.parseDouble(str); return true; } catch (Exception ignored) { return false; }
    }
}
