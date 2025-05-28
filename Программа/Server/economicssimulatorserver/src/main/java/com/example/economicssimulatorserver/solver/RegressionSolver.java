package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Универсальный Solver для линейной/множественной регрессии (OLS).
 * Принимает наблюдения (X, Y или X1, X2, ..., Y) как строки вида "1;2;3".
 * Всегда последний параметр — зависимая переменная Y, остальные — независимые.
 */
@Component
public class RegressionSolver implements Solver {

    @Override
    public SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        if (!supports(model)) {
            throw new LocalizedException("solver.unsupported_model_type");
        }

        List<ModelParameter> params = model.getParameters();
        if (params == null || params.size() < 2) {
            throw new LocalizedException("solver.regression.not_enough_parameters");
        }

        int nVars = params.size() - 1;
        List<String> paramNames = params.stream().map(ModelParameter::getName).collect(Collectors.toList());

        // Последний параметр — зависимая переменная Y
        String yName = paramNames.get(nVars);

        // Собираем данные
        List<double[]> dataColumns = new ArrayList<>();
        int dataLength = -1;
        for (ModelParameter param : params) {
            String raw = parameterValues.get(param.getId());
            if (raw == null) throw new LocalizedException("solver.regression.no_value_for_param", new Object[]{param.getName()});
            String[] parts = raw.split(";");
            if (dataLength == -1) dataLength = parts.length;
            else if (parts.length != dataLength) throw new LocalizedException("solver.regression.length_mismatch");
            double[] arr = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                try {
                    arr[i] = Double.parseDouble(parts[i].replace(',', '.'));
                } catch (NumberFormatException ex) {
                    throw new LocalizedException("solver.regression.invalid_data", ex);
                }
            }
            dataColumns.add(arr);
        }

        // X — все кроме последнего; Y — последний
        double[][] x = new double[dataLength][nVars];
        double[] y = dataColumns.get(nVars);
        for (int i = 0; i < nVars; i++) {
            double[] col = dataColumns.get(i);
            for (int j = 0; j < dataLength; j++) {
                x[j][i] = col[j];
            }
        }

        // Решаем через OLS
        double[] betas = calculateOLS(x, y);

        // Формируем результат
        StringBuilder sb = new StringBuilder();
        sb.append("b0=").append(betas[0]);
        for (int i = 1; i < betas.length; i++) {
            sb.append("; b").append(i).append("=").append(betas[i]);
        }
        return new SolverResult(sb.toString());
    }

    @Override
    public boolean supports(MathModel model) {
        // Универсально для двух типов:
        return model.getModelType() == ModelType.REGRESSION;
    }

    private double[] calculateOLS(double[][] x, double[] y) throws LocalizedException {
        int n = x.length;
        int k = x[0].length;

        double[][] xMat = new double[n][k + 1];
        for (int i = 0; i < n; i++) {
            xMat[i][0] = 1.0;
            System.arraycopy(x[i], 0, xMat[i], 1, k);
        }

        double[][] xtx = multiply(transpose(xMat), xMat);
        double[] xty = multiply(transpose(xMat), y);

        double[][] xtxInv = invert(xtx);
        if (xtxInv == null) throw new LocalizedException("solver.regression.singular_matrix");

        return multiply(xtxInv, xty);
    }

    private double[][] transpose(double[][] m) {
        int rows = m.length, cols = m[0].length;
        double[][] t = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                t[j][i] = m[i][j];
        return t;
    }

    private double[][] multiply(double[][] a, double[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;
        double[][] r = new double[m][p];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < p; j++)
                for (int k = 0; k < n; k++)
                    r[i][j] += a[i][k] * b[k][j];
        return r;
    }

    private double[] multiply(double[][] a, double[] x) {
        int m = a.length, n = a[0].length;
        double[] r = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                r[i] += a[i][j] * x[j];
        return r;
    }

    private double[][] invert(double[][] a) {
        int n = a.length;
        double[][] x = new double[n][n];
        double[][] b = new double[n][n];
        for (int i = 0; i < n; i++) {
            b[i][i] = 1;
        }
        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            if (Math.abs(pivot) < 1e-12) return null;
            for (int j = 0; j < n; j++) {
                a[i][j] /= pivot;
                b[i][j] /= pivot;
            }
            for (int k = i + 1; k < n; k++) {
                double factor = a[k][i];
                for (int j = 0; j < n; j++) {
                    a[k][j] -= factor * a[i][j];
                    b[k][j] -= factor * b[i][j];
                }
            }
        }
        for (int i = n - 1; i >= 0; i--) {
            for (int k = 0; k < i; k++) {
                double factor = a[k][i];
                for (int j = 0; j < n; j++) {
                    a[k][j] -= factor * a[i][j];
                    b[k][j] -= factor * b[i][j];
                }
            }
        }
        for (int i = 0; i < n; i++)
            System.arraycopy(b[i], 0, x[i], 0, n);
        return x;
    }
}
