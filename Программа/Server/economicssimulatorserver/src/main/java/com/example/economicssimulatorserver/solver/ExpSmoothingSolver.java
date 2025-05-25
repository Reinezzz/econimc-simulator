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
 * Solver для простого экспоненциального сглаживания.
 * Ожидает параметры "series" (массив double), "alpha" (double).
 */
@Slf4j
@Component
public class ExpSmoothingSolver extends AbstractModelSolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SolverResult solve(MathModel model) throws LocalizedException {
        double[] series = null;
        Double alpha = null;

        for (ModelParameter param : model.getParameters()) {
            if ("series".equals(param.getName()) && param.getParamType() == ParameterType.ARRAY) {
                try {
                    series = objectMapper.readValue(param.getValue(), double[].class);
                } catch (Exception e) {
                    throw new LocalizedException("error.series_parse_failed");
                }
            } else if ("alpha".equals(param.getName()) && param.getParamType() == ParameterType.DOUBLE) {
                try {
                    alpha = Double.valueOf(param.getValue());
                } catch (Exception e) {
                    throw new LocalizedException("error.alpha_parse_failed");
                }
            }
        }

        if (series == null || alpha == null) {
            throw new LocalizedException("error.exp_smoothing_missing_params");
        }
        if (alpha < 0.0 || alpha > 1.0) {
            throw new LocalizedException("error.exp_smoothing_invalid_alpha");
        }

        try {
            double[] smoothed = new double[series.length];
            smoothed[0] = series[0];
            for (int i = 1; i < series.length; i++) {
                smoothed[i] = alpha * series[i] + (1 - alpha) * smoothed[i - 1];
            }

            // Следующее значение прогноза
            double forecast = smoothed[series.length - 1];

            Map<String, Object> result = new HashMap<>();
            result.put("smoothed", smoothed);
            result.put("forecast", forecast);

            String json = objectMapper.writeValueAsString(result);

            return new SolverResult(json);
        } catch (Exception ex) {
            log.error("Exponential smoothing solve failed", ex);
            throw new LocalizedException("error.exp_smoothing_failed");
        }
    }
}
