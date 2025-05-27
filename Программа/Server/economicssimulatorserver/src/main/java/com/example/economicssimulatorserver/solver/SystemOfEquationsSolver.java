package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.exception.LocalizedException;

import org.springframework.stereotype.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (params == null || params.isEmpty()) {
            throw new LocalizedException("solver.no_parameters_in_model");
        }
        // Переменные системы
        List<String> variableNames = new ArrayList<>();
        for (ModelParameter p : params) variableNames.add(p.getName());

        // Ожидаем, что формулы разделены символом ';' или переводом строки
        String[] equations = formulaBlock.split("[;\n]");
        int n = equations.length;
        if (n != variableNames.size())
            throw new LocalizedException("solver.system.size_mismatch");

        // Строим матрицу коэффициентов и вектор правых частей (для линейной системы)
        double[][] a = new double[n][n];
        double[] b = new double[n];

        for (int i = 0; i < n; i++) {
            String eq = equations[i].replaceAll("\\s", "");
            // Пример: "2*x + 3*y = 7"
            Matcher matcher = Pattern.compile("(.+)=([\\-\\d\\.]+)$").matcher(eq);
            if (!matcher.find())
                throw new LocalizedException("solver.system.invalid_equation_format", new Object[]{eq});

            String lhs = matcher.group(1); // левая часть (коэффициенты и переменные)
            String rhs = matcher.group(2); // правая часть (число)

            // Считаем правую часть
            try {
                b[i] = Double.parseDouble(rhs.replace(',', '.'));
            } catch (Exception ex) {
                throw new LocalizedException("solver.system.invalid_rhs", new Object[]{rhs});
            }

            // Ищем коэффициенты для каждой переменной (ищем, например, "2*x", "y" и т.д.)
            for (int j = 0; j < n; j++) {
                Pattern ptn = Pattern.compile("([\\+\\-]?\\d*\\.?\\d*)\\*?" + variableNames.get(j));
                Matcher m = ptn.matcher(lhs);
                double coeff = 0;
                while (m.find()) {
                    String s = m.group(1);
                    if (s == null || s.isEmpty() || s.equals("+")) coeff += 1;
                    else if (s.equals("-")) coeff -= 1;
                    else coeff += Double.parseDouble(s.replace(',', '.'));
                }
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

    /**
     * Решение системы линейных уравнений методом Гаусса.
     */
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
            // Swap
            double[] tmp = mat[i];
            mat[i] = mat[maxRow];
            mat[maxRow] = tmp;

            if (Math.abs(mat[i][i]) < 1e-10)
                throw new LocalizedException("solver.system.singular");

            for (int k = i + 1; k < n; k++) {
                double factor = mat[k][i] / mat[i][i];
                for (int j = i; j < n + 1; j++) {
                    mat[k][j] -= factor * mat[i][j];
                }
            }
        }

        // Обратный ход
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = mat[i][n] / mat[i][i];
            for (int k = i - 1; k >= 0; k--) {
                mat[k][n] -= mat[k][i] * x[i];
            }
        }
        return x;
    }
}
