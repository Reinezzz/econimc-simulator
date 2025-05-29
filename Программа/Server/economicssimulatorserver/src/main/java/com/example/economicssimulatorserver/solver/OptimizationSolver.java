package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.exception.LocalizedException;

import org.springframework.stereotype.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class OptimizationSolver implements Solver {

    private static final int MAX_ITER = 1000;
    private static final double EPS = 1e-7;
    private static final double GRAD_STEP = 1e-5;
    private static final double LEARNING_RATE = 0.1;
    private static final double DEFAULT_MIN = -1000.0;
    private static final double DEFAULT_MAX = 1000.0;

    @Override
    public SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<SolverResult> future = executor.submit(() -> solveInternal(model, parameterValues));
        try {
            // 20 секунд на решение задачи
            return future.get(2000000000, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new LocalizedException("solver.timeout_exceeded", "Время решения задачи превысило 20 секунд. Попробуйте уменьшить диапазон поиска или упростить формулу.");
        } catch (Exception e) {
            if (e.getCause() instanceof LocalizedException) throw (LocalizedException) e.getCause();
            throw new LocalizedException("solver.unknown_error", e);
        } finally {
            executor.shutdownNow();
        }
    }

    // Основная логика Solver-а здесь
    private SolverResult solveInternal(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        if (!supports(model)) {
            throw new LocalizedException("solver.unsupported_model_type");
        }

        String formula = model.getFormula();
        if (formula == null || formula.isBlank()) {
            throw new LocalizedException("solver.formula_is_blank");
        }

        List<ModelParameter> params = model.getParameters();
        if (params == null) throw new LocalizedException("solver.no_parameters_in_model");

        // Собираем параметры-коэффициенты: имя → значение
        Map<String, Double> paramValues = params.stream()
                .collect(Collectors.toMap(
                        ModelParameter::getName,
                        p -> parseParamValue(p, parameterValues)
                ));

        // Собираем все идентификаторы из формулы
        Set<String> allVars = new HashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z_]\\w*\\b").matcher(formula);
        while (matcher.find()) {
            String var = matcher.group();
            allVars.add(var);
        }

        // Параметры модели
        Set<String> paramNames = paramValues.keySet();

        // Переменные оптимизации — те, которых нет среди параметров (т.е. x или x,y)
        List<String> optVars = allVars.stream()
                .filter(var -> !paramNames.contains(var))
                .toList();

        if (optVars.size() == 1) {
            String var = optVars.get(0);
            double min = paramValues.getOrDefault("min" + var, DEFAULT_MIN);
            double max = paramValues.getOrDefault("max" + var, DEFAULT_MAX);

            double resultX = goldenSectionMax(
                    x -> evalExpression(formula, allVars, Map.of(var, x), paramValues),
                    min, max
            );
            double resultY = evalExpression(formula, allVars, Map.of(var, resultX), paramValues);

            return new SolverResult(var + "=" + resultX + "; value=" + resultY);

        } else if (optVars.size() == 2) {
            String var1 = optVars.get(0);
            String var2 = optVars.get(1);

            double min1 = paramValues.getOrDefault("min" + var1, DEFAULT_MIN);
            double max1 = paramValues.getOrDefault("max" + var1, DEFAULT_MAX);
            double min2 = paramValues.getOrDefault("min" + var2, DEFAULT_MIN);
            double max2 = paramValues.getOrDefault("max" + var2, DEFAULT_MAX);

            double[] result = gradientDescentMax(
                    (x, y) -> evalExpression(
                            formula,
                            allVars,
                            Map.of(var1, x, var2, y),
                            paramValues
                    ),
                    (min1 + max1) / 2,
                    (min2 + max2) / 2,
                    min1, max1, min2, max2,
                    EPS, MAX_ITER
            );

            double maxVal = evalExpression(
                    formula,
                    allVars,
                    Map.of(var1, result[0], var2, result[1]),
                    paramValues
            );
            return new SolverResult(var1 + "=" + result[0] + "; " + var2 + "=" + result[1] + "; value=" + maxVal);

        } else {
            throw new LocalizedException("solver.optimization.unsupported_variables_count");
        }
    }


    @Override
    public boolean supports(MathModel model) {
        return model.getModelType() == ModelType.OPTIMIZATION;
    }

    // --- Вспомогательные методы оптимизации ---

    // Универсальный метод для вычисления выражения с произвольными переменными оптимизации
    private double evalExpression(String formula, Set<String> allVars, Map<String, Double> optValues, Map<String, Double> paramValues) throws LocalizedException {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(formula)
                    .variables(allVars); // <--- все переменные, и параметры, и оптимизируемые

            Expression expression = builder.build();

            // Сначала параметры (a, b, c)
            for (Map.Entry<String, Double> entry : paramValues.entrySet()) {
                expression.setVariable(entry.getKey(), entry.getValue());
            }
            // Теперь переменные оптимизации (x и/или y)
            for (Map.Entry<String, Double> entry : optValues.entrySet()) {
                expression.setVariable(entry.getKey(), entry.getValue());
            }

            double val = expression.evaluate();
            if (Double.isNaN(val) || Double.isInfinite(val))
                throw new LocalizedException("solver.result_invalid");
            return val;
        } catch (Exception ex) {
            throw new LocalizedException("solver.evaluation_error", ex);
        }
    }


    private double goldenSectionMax(UnaryFunction f, double a, double b) throws LocalizedException {
        final double phi = (1 + Math.sqrt(5)) / 2;
        double x1 = b - (b - a) / phi;
        double x2 = a + (b - a) / phi;
        double f1 = safeEval(f, x1);
        double f2 = safeEval(f, x2);
        int iter = 0;
        while (Math.abs(b - a) > OptimizationSolver.EPS && iter < OptimizationSolver.MAX_ITER) {
            if (f1 < f2) {
                a = x1;
                x1 = x2;
                f1 = f2;
                x2 = b - (x1 - a);
                f2 = safeEval(f, x2);
            } else {
                b = x2;
                x2 = x1;
                f2 = f1;
                x1 = a + (b - x2);
                f1 = safeEval(f, x1);
            }
            iter++;
        }
        return (a + b) / 2;
    }

    private double[] gradientDescentMax(BiFunction f, double x0, double y0,
                                        double minX, double maxX, double minY, double maxY,
                                        double eps, int maxIter) throws LocalizedException {
        double x = x0, y = y0;
        int iter = 0;
        while (iter < maxIter) {
            double gradX = (f.apply(x + GRAD_STEP, y) - f.apply(x - GRAD_STEP, y)) / (2 * GRAD_STEP);
            double gradY = (f.apply(x, y + GRAD_STEP) - f.apply(x, y - GRAD_STEP)) / (2 * GRAD_STEP);

            double newX = x + LEARNING_RATE * gradX;
            double newY = y + LEARNING_RATE * gradY;

            newX = Math.max(minX, Math.min(maxX, newX));
            newY = Math.max(minY, Math.min(maxY, newY));

            if (Math.abs(newX - x) < eps && Math.abs(newY - y) < eps) {
                break;
            }
            x = newX;
            y = newY;
            iter++;
        }
        return new double[]{x, y};
    }

    private double parseParamValue(ModelParameter p, Map<Long, String> values) {
        try {
            return Double.parseDouble(values.get(p.getId()));
        } catch (Exception ex) {
            return 0.0;
        }
    }

    private double safeEval(UnaryFunction f, double x) throws LocalizedException {
        try {
            return f.apply(x);
        } catch (Exception ex) {
            throw new LocalizedException("solver.optimization.eval_error", ex);
        }
    }

    @FunctionalInterface
    private interface UnaryFunction { double apply(double x) throws LocalizedException; }
    @FunctionalInterface
    private interface BiFunction { double apply(double x, double y) throws LocalizedException; }
}
