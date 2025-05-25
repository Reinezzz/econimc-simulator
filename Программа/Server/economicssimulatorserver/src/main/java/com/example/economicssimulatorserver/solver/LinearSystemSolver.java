package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.enums.ParameterType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.*;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;

/**
 * Solver для решения системы линейных уравнений (Ax = b).
 */
@Slf4j
@Component
public class LinearSystemSolver extends AbstractModelSolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Решает систему линейных уравнений по переданной модели.
     * Ожидает параметры: "matrix" (двумерный массив коэффициентов) и "vector" (одномерный массив свободных членов).
     *
     * @param model математическая модель
     * @return результат вычислений (JSON)
     * @throws LocalizedException если входные параметры некорректны
     */
    @Override
    public SolverResult solve(MathModel model) throws LocalizedException {
        double[][] matrix = null;
        double[] vector = null;

        // Ищем нужные параметры
        for (ModelParameter param : model.getParameters()) {
            if ("matrix".equals(param.getName()) && param.getParamType() == ParameterType.ARRAY) {
                try {
                    // Массив массивов: [[...], [...], ...]
                    matrix = objectMapper.readValue(param.getValue(), double[][].class);
                } catch (Exception e) {
                    throw new LocalizedException("error.matrix_parse_failed");
                }
            } else if ("vector".equals(param.getName()) && param.getParamType() == ParameterType.ARRAY) {
                try {
                    vector = objectMapper.readValue(param.getValue(), double[].class);
                } catch (Exception e) {
                    throw new LocalizedException("error.vector_parse_failed");
                }
            }
        }

        if (matrix == null || vector == null) {
            throw new LocalizedException("error.linear_system_missing_params");
        }

        try {
            RealMatrix coefficients = MatrixUtils.createRealMatrix(matrix);
            DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
            RealVector constants = MatrixUtils.createRealVector(vector);

            double[] solution = solver.solve(constants).toArray();

            Map<String, Object> result = new HashMap<>();
            result.put("solution", solution);
            result.put("matrix", matrix);
            result.put("vector", vector);

            String json = objectMapper.writeValueAsString(result);

            return new SolverResult(json);
        } catch (Exception ex) {
            log.error("Linear system solve failed", ex);
            throw new LocalizedException("error.linear_system_failed");
        }
    }
}
