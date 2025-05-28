package org.example.economicssimulatorclient.visualization;

import org.example.economicssimulatorclient.dto.MathModelDto;
import org.example.economicssimulatorclient.dto.ComputationResultDto;
import javafx.scene.layout.Pane;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.application.Platform;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.*;

public class OptimizationVisualizer implements Visualizer {

    private static final String CHART_LINE = "График функции";
    private static final String CHART_HEAT = "Тепловая карта";

    @Override
    public List<String> getSupportedCharts(MathModelDto model) {
        // Можно динамически определять, какие доступны (например, по числу переменных)
        Set<String> vars = extractVariables(model.formula());
        if (vars.size() == 1) return List.of(CHART_LINE);
        if (vars.size() == 2) return List.of(CHART_LINE, CHART_HEAT);
        return Collections.emptyList();
    }

    @Override
    public void visualize(MathModelDto model, ComputationResultDto result, Pane container, String chartType) {
        container.getChildren().clear();
        String formula = model.formula();
        if (formula == null || formula.isBlank()) return;

        // Собираем параметры
        Map<String, Double> paramValues = new HashMap<>();
        for (var param : model.parameters()) {
            try {
                paramValues.put(param.name(), Double.parseDouble(param.value()));
            } catch (Exception ignored) {
            }
        }
        Set<String> allVars = extractVariables(formula);
        Set<String> variables = new HashSet<>(allVars);
        variables.removeAll(paramValues.keySet());

        if (variables.size() == 1) {
            String var = variables.iterator().next();
            if (CHART_LINE.equals(chartType)) visualize1D(formula, var, paramValues, result, container);
        } else if (variables.size() == 2) {
            Iterator<String> it = variables.iterator();
            String var1 = it.next();
            String var2 = it.next();
            if (CHART_LINE.equals(chartType)) visualize2DLine(formula, var1, var2, paramValues, result, container);
            if (CHART_HEAT.equals(chartType)) visualize2DHeat(formula, var1, var2, paramValues, result, container);
        }
    }

    public String getDefaultChartType(MathModelDto model) {
        List<String> charts = getSupportedCharts(model);
        return charts.isEmpty() ? null : charts.get(0);
    }

    // ===== 1D График =====
    private void visualize1D(String formula, String var, Map<String, Double> params, ComputationResultDto result, Pane container) {
        NumberAxis xAxis = new NumberAxis(-10, 10, 1);
        xAxis.setLabel(var);
        NumberAxis yAxis = new NumberAxis(-10, 10, 2);
        yAxis.setLabel("y");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("График функции: " + formula);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (double x = -10; x <= 10; x += 0.1) {
            Map<String, Double> vars = new HashMap<>(params);
            vars.put(var, x);
            try {
                Expression exp = new ExpressionBuilder(formula)
                        .variables(vars.keySet())
                        .build()
                        .setVariables(vars);
                double y = exp.evaluate();
                if (!Double.isNaN(y) && !Double.isInfinite(y))
                    series.getData().add(new XYChart.Data<>(x, y));
            } catch (Exception ignored) {
            }
        }
        chart.getData().add(series);
        // Отмечаем экстремум (если есть)
        double optX = parseOptimum(result, var);
        if (!Double.isNaN(optX)) {
            Map<String, Double> vars = new HashMap<>(params);
            vars.put(var, optX);
            double yOpt = Double.NaN;
            try {
                Expression exp = new ExpressionBuilder(formula)
                        .variables(vars.keySet())
                        .build()
                        .setVariables(vars);
                yOpt = exp.evaluate();
            } catch (Exception ignored) {
            }
            if (!Double.isNaN(yOpt)) {
                XYChart.Data<Number, Number> marker = new XYChart.Data<>(optX, yOpt);
                series.getData().add(marker);
                Platform.runLater(() -> marker.setNode(new Circle(6, Color.RED)));
            }
        }
        container.getChildren().add(chart);
    }

    // ===== 2D Линия =====
    private void visualize2DLine(String formula, String var1, String var2, Map<String, Double> params, ComputationResultDto result, Pane container) {
        // Строит линию уровня (например, f(x, y) = 0)
        // Можно дополнительно реализовать!
        // (Пока просто вызовем тепловую карту как placeholder)
        visualize2DHeat(formula, var1, var2, params, result, container);
    }

    // ===== 2D Тепловая карта =====
    private void visualize2DHeat(String formula, String var1, String var2, Map<String, Double> params, ComputationResultDto result, Pane container) {
        NumberAxis xAxis = new NumberAxis(-10, 10, 1);
        xAxis.setLabel(var1);
        NumberAxis yAxis = new NumberAxis(-10, 10, 1);
        yAxis.setLabel(var2);
        ScatterChart<Number, Number> scatter = new ScatterChart<>(xAxis, yAxis);
        scatter.setTitle("Тепловая карта: " + formula);
        scatter.setLegendVisible(false);
        scatter.setAnimated(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double minVal = Double.POSITIVE_INFINITY;
        double maxVal = Double.NEGATIVE_INFINITY;
        Map<String, Double> vars = new HashMap<>(params);
        for (double x = -10; x <= 10; x += 0.5) {
            for (double y = -10; y <= 10; y += 0.5) {
                vars.put(var1, x);
                vars.put(var2, y);
                try {
                    Expression exp = new ExpressionBuilder(formula)
                            .variables(vars.keySet())
                            .build()
                            .setVariables(vars);
                    double z = exp.evaluate();
                    if (!Double.isNaN(z) && !Double.isInfinite(z)) {
                        minVal = Math.min(minVal, z);
                        maxVal = Math.max(maxVal, z);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        for (double x = -10; x <= 10; x += 0.5) {
            for (double y = -10; y <= 10; y += 0.5) {
                vars.put(var1, x);
                vars.put(var2, y);
                try {
                    Expression exp = new ExpressionBuilder(formula)
                            .variables(vars.keySet())
                            .build()
                            .setVariables(vars);
                    double z = exp.evaluate();
                    if (!Double.isNaN(z) && !Double.isInfinite(z)) {
                        XYChart.Data<Number, Number> d = new XYChart.Data<>(x, y);
                        series.getData().add(d);
                        double t = (z - minVal) / (maxVal - minVal + 1e-6);
                        Color color = interpolateColor(Color.BLUE, Color.RED, t);
                        Platform.runLater(() -> d.setNode(makeColorCircle(color, 4)));
                    }
                } catch (Exception ignored) {
                }
            }
        }
        scatter.getData().add(series);

        double[] optXY = parseOptimum2D(result, var1, var2);
        if (!Double.isNaN(optXY[0]) && !Double.isNaN(optXY[1])) {
            XYChart.Data<Number, Number> marker = new XYChart.Data<>(optXY[0], optXY[1]);
            series.getData().add(marker);
            Platform.runLater(() -> marker.setNode(new Circle(7, Color.GREEN)));
        }
        container.getChildren().add(scatter);
    }

    // --- Вспомогательные методы ---

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
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private double parseOptimum(ComputationResultDto result, String var) {
        return parseValueFromResult(result, var);
    }

    private double[] parseOptimum2D(ComputationResultDto result, String var1, String var2) {
        double[] xy = new double[]{Double.NaN, Double.NaN};
        if (result == null || result.resultData() == null) return xy;
        Pattern p1 = Pattern.compile(var1 + "\\s*=\\s*([-+]?\\d*\\.?\\d+)");
        Pattern p2 = Pattern.compile(var2 + "\\s*=\\s*([-+]?\\d*\\.?\\d+)");
        Matcher m1 = p1.matcher(result.resultData());
        Matcher m2 = p2.matcher(result.resultData());
        if (m1.find()) {
            try {
                xy[0] = Double.parseDouble(m1.group(1));
            } catch (Exception ignored) {
            }
        }
        if (m2.find()) {
            try {
                xy[1] = Double.parseDouble(m2.group(1));
            } catch (Exception ignored) {
            }
        }
        return xy;
    }

    private Color interpolateColor(Color color1, Color color2, double t) {
        return new Color(
                color1.getRed() + (color2.getRed() - color1.getRed()) * t,
                color1.getGreen() + (color2.getGreen() - color1.getGreen()) * t,
                color1.getBlue() + (color2.getBlue() - color1.getBlue()) * t,
                1.0
        );
    }

    private Circle makeColorCircle(Color color, double radius) {
        Circle circle = new Circle(radius, color);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(0.5);
        return circle;
    }


}
