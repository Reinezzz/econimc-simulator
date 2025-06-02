package com.example.economicssimulatorserver.solver;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SolverFactory {

    private final Map<String, EconomicModelSolver> solverMap = new ConcurrentHashMap<>();

    /**
     * Внедряются все реализованные солверы автоматически через Spring.
     */
    public SolverFactory(java.util.List<EconomicModelSolver> solvers) {
        for (EconomicModelSolver solver : solvers) {
            solverMap.put(solver.getModelType(), solver);
        }
    }

    /**
     * Получить солвер по типу модели
     */
    public EconomicModelSolver getSolver(String modelType) {
        return solverMap.get(modelType);
    }
}
