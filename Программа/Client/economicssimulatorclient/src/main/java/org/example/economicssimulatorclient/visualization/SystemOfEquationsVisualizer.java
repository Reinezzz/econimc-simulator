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

import java.util.*;
import java.util.regex.*;

public class SystemOfEquationsVisualizer implements Visualizer {

    private static final String CHART_SYSTEM = "График системы";
    private static final String CHART_SOLUTION = "Система + решение";

    @Override
    public List<String> getSupportedCharts(MathModelDto model) {
        return List.of(CHART_SYSTEM, CHART_SOLUTION);
    }

    @Override
    public void visualize(MathModelDto model, ComputationResultDto result, Pane container, String chartType) {
        container.getChildren().clear();
        String formula = model.formula();
        if (formula == null || formula.isBlank()) return;

        // 1. Разбиваем на отдельные уравнения (по ; или переносу строки)
        String[] equations = formula.split("[;\n]+");

        // 2. Собираем переменные (например, x и y)
        Set<String> allVars = new LinkedHashSet<>();
        for (String eq : equations) allVars.addAll(extractVariables(eq));
        if (allVars.size() != 2) return;

        Iterator<String> it = allVars.iterator();
        String var1 = it.next();
        String var2 = it.next();

        NumberAxis xAxis = new NumberAxis(-10, 10, 1);
        xAxis.setLabel(var1);
        NumberAxis yAxis = new NumberAxis(-10, 10, 1);
        yAxis.setLabel(var2);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setTitle(chartType);

        // Для каждого уравнения строим линию в координатах x/y
        for (String eq : equations) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            String[] parts = eq.split("=");
            if (parts.length != 2) continue;
            String left = parts[0].trim();
            String right = parts[1].trim();

            // Пробуем выразить y как функцию x, чтобы строить линию.
            for (double x = -10; x <= 10; x += 0.1) {
                Map<String, Double> vars = new HashMap<>();
                vars.put(var1, x);
                try {
                    // Пробуем вычислить y для данного x (по формуле: left(x, y) = right)
                    // y = (right - выражение без y) / коэффициент при y (если можно выделить y)
                    // Для клиента: допускаем только форматы типа y = f(x), либо ax + by = c (если простая форма)
                    // Поэтому тут строим только при наличии явного y = ...
                    if (left.equals(var2)) {
                        double yVal = eval(right, var1, x);
                        series.getData().add(new XYChart.Data<>(x, yVal));
                    } else if (right.equals(var2)) {
                        double yVal = eval(left, var1, x);
                        series.getData().add(new XYChart.Data<>(x, yVal));
                    }
                } catch (Exception ignored) {}
            }
            chart.getData().add(series);
        }

        // Если есть решение — отмечаем на графике
        if (chartType.equals(CHART_SOLUTION)) {
            double[] solution = parseSolution(result, var1, var2);
            if (!Double.isNaN(solution[0]) && !Double.isNaN(solution[1])) {
                XYChart.Series<Number, Number> markerSeries = new XYChart.Series<>();
                XYChart.Data<Number, Number> marker = new XYChart.Data<>(solution[0], solution[1]);
                markerSeries.getData().add(marker);
                chart.getData().add(markerSeries);
                Platform.runLater(() -> marker.setNode(new Circle(7, Color.GREEN)));
            }
        }
        container.getChildren().add(chart);
    }

    @Override
    public String getDefaultChartType(MathModelDto model) {
        return CHART_SYSTEM;
    }

    // --- Вспомогательные методы ---

    // Ищет только имена переменных, не числа
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

    // Простой eval выражения типа f(x) (только если есть одна переменная)
    private double eval(String expr, String var, double val) {
        return new net.objecthunter.exp4j.ExpressionBuilder(expr)
                .variables(var)
                .build()
                .setVariable(var, val)
                .evaluate();
    }

    private double[] parseSolution(ComputationResultDto result, String var1, String var2) {
        double[] xy = new double[] {Double.NaN, Double.NaN};
        if (result == null || result.resultData() == null) return xy;
        Pattern p1 = Pattern.compile(var1 + "\\s*=\\s*([-+]?\\d*\\.?\\d+)");
        Pattern p2 = Pattern.compile(var2 + "\\s*=\\s*([-+]?\\d*\\.?\\d+)");
        Matcher m1 = p1.matcher(result.resultData());
        Matcher m2 = p2.matcher(result.resultData());
        if (m1.find()) try { xy[0] = Double.parseDouble(m1.group(1)); } catch (Exception ignored) {}
        if (m2.find()) try { xy[1] = Double.parseDouble(m2.group(1)); } catch (Exception ignored) {}
        return xy;
    }
}
