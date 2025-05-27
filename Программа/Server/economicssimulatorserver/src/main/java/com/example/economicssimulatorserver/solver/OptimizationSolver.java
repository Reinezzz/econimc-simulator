package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.exception.LocalizedException;

import org.springframework.stereotype.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Solver для оптимизационных задач: одномерных и двумерных.
 * Для одномерных задач — метод золотого сечения.
 * Для двумерных — градиентный спуск (простой вариант).
 */
@Component
public class OptimizationSolver implements Solver {

    private static final int MAX_ITER = 1000;
    private static final double EPS = 1e-7;
    private static final double GRAD_STEP = 1e-5;
    private static final double LEARNING_RATE = 0.1;

    @Override
    public SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        if (!supports(model)) {
            throw new LocalizedException("solver.unsupported_model_type");
        }

        String formula = model.getFormula();
        if (formula == null || formula.isBlank()) {
            throw new LocalizedException("solver.formula_is_blank");
        }

        List<ModelParameter> params = model.getParameters();
        if (params == null || params.isEmpty()) {
            throw new LocalizedException("solver.no_parameters_in_model");
        }

        // Выделяем параметры-оптимизируемые переменные

        if (params.size() == 1) {
            // Одномерная оптимизация
            return solveOneDimensional(formula, params, parameterValues, params.get(0));
        } else if (params.size() == 2) {
            // Двумерная оптимизация
            return solveTwoDimensional(formula, params, parameterValues, params.get(0), params.get(1));
        } else {
            throw new LocalizedException("solver.optimization.unsupported_variables_count");
        }
    }

    @Override
    public boolean supports(MathModel model) {
        return model.getModelType() == ModelType.OPTIMIZATION;
    }

    private SolverResult solveOneDimensional(String formula,
                                             List<ModelParameter> params,
                                             Map<Long, String> parameterValues,
                                             ModelParameter variable)
            throws LocalizedException {

        String varName = variable.getName();

        // Диапазон поиска (можно задать minX, maxX, иначе дефолт)
        double lower = getBoundary("min" + varName, params, parameterValues, -1000.0);
        double upper = getBoundary("max" + varName, params, parameterValues, 1000.0);

        // Подставляем все параметры (кроме оптимизируемого)
        Map<String, Double> fixedParams = params.stream()
                .filter(p -> !p.getName().equals(varName))
                .collect(Collectors.toMap(
                        ModelParameter::getName,
                        p -> parseParamValue(p, parameterValues)
                ));

        // Метод золотого сечения для поиска максимума
        double resultX = goldenSectionMax(
                x -> evalExpression(formula, varName, x, fixedParams),
                lower, upper, EPS, MAX_ITER
        );
        double resultY = evalExpression(formula, varName, resultX, fixedParams);

        return new SolverResult(varName + "=" + resultX + "; value=" + resultY);
    }

    private SolverResult solveTwoDimensional(String formula,
                                             List<ModelParameter> params,
                                             Map<Long, String> parameterValues,
                                             ModelParameter v1,
                                             ModelParameter v2)
            throws LocalizedException {

        String name1 = v1.getName();
        String name2 = v2.getName();

        double lower1 = getBoundary("min" + name1, params, parameterValues, -1000.0);
        double upper1 = getBoundary("max" + name1, params, parameterValues, 1000.0);
        double lower2 = getBoundary("min" + name2, params, parameterValues, -1000.0);
        double upper2 = getBoundary("max" + name2, params, parameterValues, 1000.0);

        Map<String, Double> fixedParams = params.stream()
                .filter(p -> !p.getName().equals(name1) && !p.getName().equals(name2))
                .collect(Collectors.toMap(
                        ModelParameter::getName,
                        p -> parseParamValue(p, parameterValues)
                ));

        // Градиентный спуск (максимум)
        double[] result = gradientDescentMax(
                (x, y) -> evalExpression(formula, name1, x, name2, y, fixedParams),
                (upper1 + lower1) / 2,
                (upper2 + lower2) / 2,
                lower1, upper1, lower2, upper2,
                EPS, MAX_ITER
        );

        double maxVal = evalExpression(formula, name1, result[0], name2, result[1], fixedParams);
        return new SolverResult(name1 + "=" + result[0] + "; " + name2 + "=" + result[1] + "; value=" + maxVal);
    }

    // ----- Алгебра/оптимизация -----

    // Золотое сечение для поиска максимума
    private double goldenSectionMax(UnaryFunction f, double a, double b, double eps, int maxIter) throws LocalizedException {
        final double phi = (1 + Math.sqrt(5)) / 2;
        double x1 = b - (b - a) / phi;
        double x2 = a + (b - a) / phi;
        double f1 = safeEval(f, x1);
        double f2 = safeEval(f, x2);
        int iter = 0;
        while (Math.abs(b - a) > eps && iter < maxIter) {
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

    // Простой градиентный спуск для поиска максимума (двумерный)
    private double[] gradientDescentMax(BiFunction f, double x0, double y0,
                                        double minX, double maxX, double minY, double maxY,
                                        double eps, int maxIter) throws LocalizedException {
        double x = x0, y = y0;
        int iter = 0;
        while (iter < maxIter) {
            // Производные по x и y
            double gradX = (f.apply(x + GRAD_STEP, y) - f.apply(x - GRAD_STEP, y)) / (2 * GRAD_STEP);
            double gradY = (f.apply(x, y + GRAD_STEP) - f.apply(x, y - GRAD_STEP)) / (2 * GRAD_STEP);

            // Обновление (максимизация!)
            double newX = x + LEARNING_RATE * gradX;
            double newY = y + LEARNING_RATE * gradY;

            // Ограничение по диапазону
            newX = Math.max(minX, Math.min(maxX, newX));
            newY = Math.max(minY, Math.min(maxY, newY));

            // Проверка сходимости
            if (Math.abs(newX - x) < eps && Math.abs(newY - y) < eps) {
                break;
            }
            x = newX;
            y = newY;
            iter++;
        }
        return new double[]{x, y};
    }

    // --- Вспомогательные методы ---

    private double evalExpression(String formula, String varName, double value, Map<String, Double> fixedParams) throws LocalizedException {
        Map<String, Double> variables = new java.util.HashMap<>(fixedParams);
        variables.put(varName, value);
        return evalExpression(formula, variables);
    }

    private double evalExpression(String formula, String var1, double value1, String var2, double value2, Map<String, Double> fixedParams) throws LocalizedException {
        Map<String, Double> variables = new java.util.HashMap<>(fixedParams);
        variables.put(var1, value1);
        variables.put(var2, value2);
        return evalExpression(formula, variables);
    }

    private double evalExpression(String formula, Map<String, Double> variables) throws LocalizedException {
        try {
            Expression expression = new ExpressionBuilder(formula)
                    .variables(variables.keySet())
                    .build()
                    .setVariables(variables);
            double val = expression.evaluate();
            if (Double.isNaN(val) || Double.isInfinite(val))
                throw new LocalizedException("solver.result_invalid");
            return val;
        } catch (Exception ex) {
            throw new LocalizedException("solver.evaluation_error", ex);
        }
    }

    private double getBoundary(String name, List<ModelParameter> params, Map<Long, String> values, double def) {
        return params.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(p -> {
                    try {
                        return Double.parseDouble(values.get(p.getId()));
                    } catch (Exception e) {
                        return def;
                    }
                })
                .orElse(def);
    }

    private double parseParamValue(ModelParameter p, Map<Long, String> values) {
        try {
            return Double.parseDouble(values.get(p.getId()));
        } catch (Exception ex) {
            return 0.0;
        }
    }

    private String getParamNameById(List<ModelParameter> params, Long id) {
        return params.stream()
                .filter(p -> p.getId().equals(id))
                .map(ModelParameter::getName)
                .findFirst()
                .orElse("");
    }

    // Безопасное вычисление функции с обработкой ошибок
    private double safeEval(UnaryFunction f, double x) throws LocalizedException {
        try {
            return f.apply(x);
        } catch (Exception ex) {
            throw new LocalizedException("solver.optimization.eval_error", ex);
        }
    }

    // --- Функциональные интерфейсы ---
    @FunctionalInterface
    private interface UnaryFunction { double apply(double x) throws LocalizedException; }
    @FunctionalInterface
    private interface BiFunction { double apply(double x, double y) throws LocalizedException; }
}
