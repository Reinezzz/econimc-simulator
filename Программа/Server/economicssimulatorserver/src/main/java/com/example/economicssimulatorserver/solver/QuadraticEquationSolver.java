package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.enums.ParameterType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Solver для решения квадратного уравнения (ax^2 + bx + c = 0).
 * Ожидает параметры "a", "b", "c" (все DOUBLE).
 */
@Slf4j
@Component
public class QuadraticEquationSolver extends AbstractModelSolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SolverResult solve(MathModel model) throws LocalizedException {
        Double a = null, b = null, c = null;

        for (ModelParameter param : model.getParameters()) {
            if ("a".equals(param.getName()) && param.getParamType() == ParameterType.DOUBLE) {
                a = Double.valueOf(param.getValue());
            } else if ("b".equals(param.getName()) && param.getParamType() == ParameterType.DOUBLE) {
                b = Double.valueOf(param.getValue());
            } else if ("c".equals(param.getName()) && param.getParamType() == ParameterType.DOUBLE) {
                c = Double.valueOf(param.getValue());
            }
        }

        if (a == null || b == null || c == null) {
            throw new LocalizedException("error.quadratic_eq_missing_params");
        }

        try {
            double discriminant = b * b - 4 * a * c;
            Map<String, Object> result = new HashMap<>();

            if (discriminant > 0) {
                double x1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                double x2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                result.put("roots", new double[]{x1, x2});
                result.put("discriminant", discriminant);
                result.put("type", "two real roots");
            } else if (discriminant == 0) {
                double x = -b / (2 * a);
                result.put("roots", new double[]{x});
                result.put("discriminant", discriminant);
                result.put("type", "one real root");
            } else {
                result.put("roots", new double[]{});
                result.put("discriminant", discriminant);
                result.put("type", "no real roots");
            }

            String json = objectMapper.writeValueAsString(result);

            return new SolverResult(json);
        } catch (Exception ex) {
            log.error("Quadratic equation solve failed", ex);
            throw new LocalizedException("error.quadratic_eq_failed");
        }
    }
}
