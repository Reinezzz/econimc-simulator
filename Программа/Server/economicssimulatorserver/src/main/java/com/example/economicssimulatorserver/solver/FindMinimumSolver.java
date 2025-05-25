package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.enums.ParameterType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.*;
import org.apache.commons.math3.optim.*;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Solver для поиска минимума простой функции на отрезке.
 * Ожидает параметры "function" (имя функции: "quadratic", "sin", "cos", "exp"), "lower" (double), "upper" (double).
 */
@Slf4j
@Component
public class FindMinimumSolver extends AbstractModelSolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SolverResult solve(MathModel model) throws LocalizedException {
        String functionName = null;
        Double lower = null, upper = null;

        for (ModelParameter param : model.getParameters()) {
            if ("function".equals(param.getName()) && param.getParamType() == ParameterType.STRING) {
                functionName = param.getValue();
            } else if ("lower".equals(param.getName()) && param.getParamType() == ParameterType.DOUBLE) {
                lower = Double.valueOf(param.getValue());
            } else if ("upper".equals(param.getName()) && param.getParamType() == ParameterType.DOUBLE) {
                upper = Double.valueOf(param.getValue());
            }
        }

        if (functionName == null || lower == null || upper == null) {
            throw new LocalizedException("error.find_minimum_missing_params");
        }
        if (lower >= upper) {
            throw new LocalizedException("error.find_minimum_invalid_bounds");
        }

        try {
            UnivariateFunction function;
            switch (functionName) {
                case "quadratic":
                    function = x -> x * x - 4 * x + 3;
                    break;
                case "sin":
                    function = new Sin();
                    break;
                case "cos":
                    function = new Cos();
                    break;
                case "exp":
                    function = new Exp();
                    break;
                default:
                    throw new LocalizedException("error.find_minimum_unsupported_function");
            }

            UnivariateOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
            UnivariatePointValuePair result = optimizer.optimize(
                    new MaxEval(1000),
                    new UnivariateObjectiveFunction(function),
                    GoalType.MINIMIZE,
                    new SearchInterval(lower, upper)
            );

            Map<String, Object> out = new HashMap<>();
            out.put("minimumX", result.getPoint());
            out.put("minimumY", result.getValue());

            String json = objectMapper.writeValueAsString(out);

            return new SolverResult(json);
        } catch (Exception ex) {
            log.error("Find minimum solve failed", ex);
            throw new LocalizedException("error.find_minimum_failed");
        }
    }
}
