package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.enums.ModelType;
import com.example.economicssimulatorserver.exception.LocalizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика вычислителей для различных типов математических моделей.
 */
@Component
@RequiredArgsConstructor
public class SolverFactory {

    private final LinearSystemSolver linearSystemSolver;
    private final LinearRegressionSolver linearRegressionSolver;
    private final ExpSmoothingSolver expSmoothingSolver;
    private final FindMinimumSolver findMinimumSolver;
    private final QuadraticEquationSolver quadraticEquationSolver;

    private final Map<ModelType, AbstractModelSolver> solverMap = new EnumMap<>(ModelType.class);

    /**
     * Получает solver по типу модели.
     * @param modelType тип модели
     * @return конкретный solver
     */
    public AbstractModelSolver getSolver(ModelType modelType) {
        if (solverMap.isEmpty()) {
            solverMap.put(ModelType.LINEAR_SYSTEM, linearSystemSolver);
            solverMap.put(ModelType.LINEAR_REGRESSION, linearRegressionSolver);
            solverMap.put(ModelType.EXP_SMOOTHING, expSmoothingSolver);
            solverMap.put(ModelType.FIND_MINIMUM, findMinimumSolver);
            solverMap.put(ModelType.QUADRATIC_EQUATION, quadraticEquationSolver);
        }
        AbstractModelSolver solver = solverMap.get(modelType);
        if (solver == null) {
            throw new LocalizedException("error.unsupported_model_type");
        }
        return solver;
    }
}
