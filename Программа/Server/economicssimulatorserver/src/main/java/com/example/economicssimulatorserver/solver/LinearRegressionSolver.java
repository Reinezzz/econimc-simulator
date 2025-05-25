package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.enums.ParameterType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Solver для простой линейной регрессии (Y = aX + b).
 * Ожидает параметры "x" и "y" (оба — массивы double).
 */
@Slf4j
@Component
public class LinearRegressionSolver extends AbstractModelSolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SolverResult solve(MathModel model) throws LocalizedException {
        double[] x = null, y = null;

        for (ModelParameter param : model.getParameters()) {
            if ("x".equals(param.getName()) && param.getParamType() == ParameterType.ARRAY) {
                try {
                    x = objectMapper.readValue(param.getValue(), double[].class);
                } catch (Exception e) {
                    throw new LocalizedException("error.x_parse_failed");
                }
            } else if ("y".equals(param.getName()) && param.getParamType() == ParameterType.ARRAY) {
                try {
                    y = objectMapper.readValue(param.getValue(), double[].class);
                } catch (Exception e) {
                    throw new LocalizedException("error.y_parse_failed");
                }
            }
        }
        if (x == null || y == null) {
            throw new LocalizedException("error.linear_regression_missing_params");
        }
        if (x.length != y.length) {
            throw new LocalizedException("error.linear_regression_length_mismatch");
        }

        try {
            SimpleRegression regression = new SimpleRegression();
            for (int i = 0; i < x.length; i++) {
                regression.addData(x[i], y[i]);
            }

            double slope = regression.getSlope();
            double intercept = regression.getIntercept();
            double r = regression.getR();
            double r2 = regression.getRSquare();

            Map<String, Object> result = new HashMap<>();
            result.put("slope", slope);
            result.put("intercept", intercept);
            result.put("correlation", r);
            result.put("r2", r2);

            String json = objectMapper.writeValueAsString(result);

            return new SolverResult(json);
        } catch (Exception ex) {
            log.error("Linear regression solve failed", ex);
            throw new LocalizedException("error.linear_regression_failed");
        }
    }
}
