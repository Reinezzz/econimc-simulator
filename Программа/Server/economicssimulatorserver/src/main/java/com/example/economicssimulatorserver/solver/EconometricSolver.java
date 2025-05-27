package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.exception.LocalizedException;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Solver для эконометрических моделей (пример — множественная линейная регрессия)
 */
@Component
public class EconometricSolver implements Solver {

    @Override
    public SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        if (!supports(model)) {
            throw new LocalizedException("solver.unsupported_model_type");
        }

        // Примерный формат: параметры - имена X1, X2, ..., Y;
        // Значения: строки вида "1,2,3;4,5,6;..." — то есть каждый параметр хранит все значения через ;
        List<ModelParameter> params = model.getParameters();
        if (params == null || params.size() < 2) {
            throw new LocalizedException("solver.econometric.not_enough_parameters");
        }

        // Собираем имена параметров
        List<String> paramNames = params.stream()
                .map(ModelParameter::getName)
                .collect(Collectors.toList());

        // Считаем, что последний параметр — это Y, остальные — X1, X2, ...
        int nPredictors = params.size() - 1;
        String yName = paramNames.get(nPredictors);

        // Получаем данные для каждого параметра
        List<double[]> dataColumns = new ArrayList<>();
        int dataLength = -1;
        for (ModelParameter param : params) {
            String raw = parameterValues.get(param.getId());
            if (raw == null) throw new LocalizedException("solver.econometric.no_value_for_param", new Object[]{param.getName()});
            String[] parts = raw.split(";");
            if (dataLength == -1) dataLength = parts.length;
            else if (parts.length != dataLength) throw new LocalizedException("solver.econometric.length_mismatch");
            double[] arr = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                try {
                    arr[i] = Double.parseDouble(parts[i].replace(',', '.'));
                } catch (NumberFormatException ex) {
                    throw new LocalizedException("solver.econometric.invalid_data", ex);
                }
            }
            dataColumns.add(arr);
        }

        // Формируем X и Y
        double[][] x = new double[dataLength][nPredictors];
        double[] y = dataColumns.get(nPredictors); // Y — последний столбец
        for (int i = 0; i < nPredictors; i++) {
            double[] col = dataColumns.get(i);
            for (int j = 0; j < dataLength; j++) {
                x[j][i] = col[j];
            }
        }

        // Считаем коэффициенты методом МНК (OLS)
        double[] betas = calculateOLS(x, y);

        // Формируем строку ответа: b0, b1, b2, ...
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < betas.length; i++) {
            sb.append("b").append(i).append("=").append(betas[i]);
            if (i < betas.length - 1) sb.append("; ");
        }
        return new SolverResult(sb.toString());
    }

    @Override
    public boolean supports(MathModel model) {
        return model.getModelType() == ModelType.ECONOMETRIC;
    }

    /**
     * Решение множественной линейной регрессии по формуле OLS:
     * b = (X^T X)^(-1) X^T y
     * @param x — матрица независимых переменных (N x K)
     * @param y — вектор зависимой переменной (N)
     * @return коэффициенты b (K+1), включая свободный член
     * @throws LocalizedException если невозможно вычислить коэффициенты
     */
    private double[] calculateOLS(double[][] x, double[] y) throws LocalizedException {
        int n = x.length;
        int k = x[0].length;

        // Добавляем столбец единиц (для b0)
        double[][] xMat = new double[n][k + 1];
        for (int i = 0; i < n; i++) {
            xMat[i][0] = 1.0;
            System.arraycopy(x[i], 0, xMat[i], 1, k);
        }

        // Считаем (X^T X)
        double[][] xtx = multiply(transpose(xMat), xMat);
        double[] xty = multiply(transpose(xMat), y);

        // Находим обратную матрицу
        double[][] xtxInv = invert(xtx);
        if (xtxInv == null) throw new LocalizedException("solver.econometric.singular_matrix");

        // Получаем коэффициенты b = (X^T X)^-1 X^T y
        return multiply(xtxInv, xty);
    }

    // --- Вспомогательные матричные методы ниже ---

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

    // Наивный метод обращения матрицы (Гаусса-Жордана, без проверок на вырожденность)
    private double[][] invert(double[][] a) {
        int n = a.length;
        double[][] x = new double[n][n];
        double[][] b = new double[n][n];
        for (int i = 0; i < n; i++) {
            b[i][i] = 1;
        }
        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            if (Math.abs(pivot) < 1e-12) return null; // сингулярная матрица
            for (int j = 0; j < n; j++) {
                a[i][j] /= pivot;
                b[i][j] /= pivot;
            }
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
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
