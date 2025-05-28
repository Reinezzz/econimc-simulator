package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.springframework.stereotype.Component;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SystemOfEquationsSolver implements Solver {

    @Override
    public SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        if (!supports(model)) {
            throw new LocalizedException("solver.unsupported_model_type");
        }

        String formulaBlock = model.getFormula();
        if (formulaBlock == null || formulaBlock.isBlank()) {
            throw new LocalizedException("solver.formula_is_blank");
        }

        List<ModelParameter> params = model.getParameters();
        if (params == null) throw new LocalizedException("solver.no_parameters_in_model");

        // Собираем параметры-коэффициенты: имя -> значение
        Map<String, Double> paramValues = params.stream()
                .collect(Collectors.toMap(
                        ModelParameter::getName,
                        p -> parseParamValue(p, parameterValues)
                ));

        // Ожидаем формулы через ";" или перевод строки
        String[] equations = formulaBlock.split("[;\n]");
        int n = equations.length;

        // Определяем переменные системы — те, что встречаются в уравнениях, но не среди параметров (exp4j)
        Set<String> allVars = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b[a-zA-Z_]\\w*\\b");

        for (String eq : equations) {
            String left = eq.split("=")[0];
            Matcher matcher = pattern.matcher(left);
            while (matcher.find()) {
                String var = matcher.group();
                allVars.add(var);
            }
        }
        // Оставляем только те, которых нет среди параметров — это искомые переменные
        List<String> variableNames = allVars.stream()
                .filter(v -> !paramValues.containsKey(v))
                .sorted()
                .toList();


        if (variableNames.size() != n) {
            throw new LocalizedException("solver.system.size_mismatch",
                    "Число переменных (" + variableNames.size() + ") не совпадает с числом уравнений (" + n + ")");
        }

        // Строим матрицу коэффициентов и вектор правых частей
        double[][] a = new double[n][n];
        double[] b = new double[n];

        for (int i = 0; i < n; i++) {
            String eq = equations[i].replaceAll("\\s", "");
            String[] parts = eq.split("=");
            if (parts.length != 2)
                throw new LocalizedException("solver.system.invalid_equation_format", new Object[]{eq});

            String lhs = parts[0]; // левая часть (коэффициенты и переменные)
            String rhs = parts[1]; // правая часть (выражение или число)

            // Вычисляем правую часть (может быть выражением через параметры)
            b[i] = evalExpression(rhs, paramValues);

            // Вычисляем коэффициенты переменных
            for (int j = 0; j < n; j++) {
                String var = variableNames.get(j);

                // Находим коэффициент при переменной var (например, "a1" при x)
                double coeff = extractCoefficient(lhs, var, paramValues);
                a[i][j] = coeff;
            }
        }

        // Решаем систему методом Гаусса
        double[] solution = solveLinearSystem(a, b);

        // Формируем результат
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(variableNames.get(i)).append("=").append(solution[i]);
            if (i < n - 1) sb.append("; ");
        }
        return new SolverResult(sb.toString());
    }

    @Override
    public boolean supports(MathModel model) {
        return model.getModelType() == ModelType.SYSTEM_OF_EQUATIONS;
    }

    // --- Вспомогательные методы ---

    // Выражение с параметрами (exp4j)
    private double evalExpression(String expr, Map<String, Double> paramValues) throws LocalizedException {
        try {
            return new ExpressionBuilder(expr)
                    .variables(paramValues.keySet())
                    .build()
                    .setVariables(paramValues)
                    .evaluate();
        } catch (Exception ex) {
            throw new LocalizedException("solver.system.invalid_rhs", ex);
        }
    }

    // Извлекает коэффициент при переменной, учитывая параметры
    private double extractCoefficient(String lhs, String var, Map<String, Double> paramValues) throws LocalizedException {
        // Паттерн ищет, например, a1*x, +b2*x, -c1*x, x (без коэфф.), +x, -x
        Pattern ptn = Pattern.compile("([\\+\\-]?\\w*)\\*?" + var);
        Matcher m = ptn.matcher(lhs);
        double coeff = 0;
        while (m.find()) {
            String name = m.group(1).trim();
            if (name.isEmpty() || name.equals("+")) coeff += 1;
            else if (name.equals("-")) coeff -= 1;
            else {
                // name может быть вида "+b1" или "-b1"
                String normName = name.replaceFirst("^[+]", ""); // убираем +
                double sign = 1.0;
                if (normName.startsWith("-")) {
                    sign = -1.0;
                    normName = normName.substring(1);
                }
                Double val = paramValues.get(normName);
                if (val == null) throw new LocalizedException("solver.system.missing_coefficient", normName);
                coeff += sign * val;
            }
        }
        return coeff;
    }


    private double parseParamValue(ModelParameter p, Map<Long, String> values) {
        try {
            return Double.parseDouble(values.get(p.getId()));
        } catch (Exception ex) {
            return 0.0;
        }
    }

    // Метод Гаусса
    private double[] solveLinearSystem(double[][] a, double[] b) throws LocalizedException {
        int n = a.length;
        double[][] mat = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, mat[i], 0, n);
            mat[i][n] = b[i];
        }
        // Прямой ход
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(mat[k][i]) > Math.abs(mat[maxRow][i])) {
                    maxRow = k;
                }
            }
            double[] tmp = mat[i]; mat[i] = mat[maxRow]; mat[maxRow] = tmp;
            if (Math.abs(mat[i][i]) < 1e-10)
                throw new LocalizedException("solver.system.singular");
            for (int k = i + 1; k < n; k++) {
                double factor = mat[k][i] / mat[i][i];
                for (int j = i; j < n + 1; j++)
                    mat[k][j] -= factor * mat[i][j];
            }
        }
        // Обратный ход
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = mat[i][n] / mat[i][i];
            for (int k = i - 1; k >= 0; k--)
                mat[k][n] -= mat[k][i] * x[i];
        }
        return x;
    }
}
