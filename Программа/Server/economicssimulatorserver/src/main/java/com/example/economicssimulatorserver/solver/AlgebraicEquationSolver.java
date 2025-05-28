package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.enums.ModelType;

import org.springframework.stereotype.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class AlgebraicEquationSolver implements Solver {

    private static final int MAX_ITER = 1000;
    private static final double EPS = 1e-8;

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

        // Ожидаем уравнение в виде "выражение = число"
        String[] parts = formula.split("=");
        if (parts.length != 2) {
            throw new LocalizedException("solver.invalid_equation_format");
        }
        String leftExpr = parts[0].trim();
        String rightExpr = parts[1].trim();

        // Сопоставляем параметры: коэффициенты, правая часть
        Map<String, Double> paramValues = params.stream()
                .collect(Collectors.toMap(
                        ModelParameter::getName,
                        p -> {
                            String valueStr = parameterValues.get(p.getId());
                            if (valueStr == null) {
                                throw new RuntimeException("solver.parameter_not_found:" + p.getName());
                            }
                            try {
                                return Double.parseDouble(valueStr);
                            } catch (NumberFormatException ex) {
                                throw new RuntimeException("solver.invalid_parameter:" + p.getName());
                            }
                        }
                ));

        // Получаем все переменные из выражения (без передачи .variables(...))
        Set<String> variablesInExpr = new HashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z_]\\w*\\b").matcher(leftExpr);
        while (matcher.find()) {
            String var = matcher.group();
            // Можно добавить фильтр по функциям exp4j, если хочешь
            variablesInExpr.add(var);
        }


        // Находим неизвестную переменную (например, x)
        String unknown = variablesInExpr.stream()
                .filter(var -> !paramValues.containsKey(var))
                .findFirst()
                .orElseThrow(() -> new LocalizedException("solver.no_unknown_variable"));


        // Численное решение уравнения: f(x) = left - right = 0
        double rightValue = evalExpression(rightExpr, paramValues);

        double root = findRoot(
                x -> evalExpression(leftExpr, paramValues, unknown, x) - rightValue,
                -1e6, 1e6, EPS, MAX_ITER
        );

        return new SolverResult(unknown + "=" + root);
    }

    @Override
    public boolean supports(MathModel model) {
        return model.getModelType() == ModelType.ALGEBRAIC_EQUATION;
    }

    // Вычисление выражения с параметрами
    private double evalExpression(String expr, Map<String, Double> paramValues) throws LocalizedException {
        try {
            Expression e = new ExpressionBuilder(expr)
                    .variables(paramValues.keySet())
                    .build()
                    .setVariables(paramValues);
            return e.evaluate();
        } catch (Exception ex) {
            throw new LocalizedException("solver.evaluation_error", ex);
        }
    }

    // Перегрузка с одной неизвестной
    private double evalExpression(String expr, Map<String, Double> paramValues, String unknown, double x) throws LocalizedException {
        Map<String, Double> vars = new java.util.HashMap<>(paramValues);
        vars.put(unknown, x);
        return evalExpression(expr, vars);
    }

    // Простой метод бисекции для поиска корня
    private double findRoot(UnaryFunction f, double a, double b, double eps, int maxIter) throws LocalizedException {
        double fa = f.apply(a), fb = f.apply(b);
        if (fa * fb > 0) throw new LocalizedException("solver.no_root_on_interval");
        for (int i = 0; i < maxIter; i++) {
            double c = (a + b) / 2;
            double fc = f.apply(c);
            if (Math.abs(fc) < eps) return c;
            if (fa * fc < 0) {
                b = c; fb = fc;
            } else {
                a = c; fa = fc;
            }
        }
        throw new LocalizedException("solver.root_not_found");
    }

    @FunctionalInterface
    private interface UnaryFunction { double apply(double x) throws LocalizedException; }
}
