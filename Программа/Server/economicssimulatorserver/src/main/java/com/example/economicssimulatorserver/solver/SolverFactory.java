package com.example.economicssimulatorserver.solver;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Фабрика, хранящая солверы и выдающая их по типу модели.
 */
@Component
public class SolverFactory {

    private final Map<String, EconomicModelSolver> solverMap = new ConcurrentHashMap<>();

    /**
     * Регистрирует все доступные солверы.
     */
    public SolverFactory(java.util.List<EconomicModelSolver> solvers) {
        for (EconomicModelSolver solver : solvers) {
            solverMap.put(solver.getModelType(), solver);
        }
    }

    /**
     * Возвращает солвер по его идентификатору.
     *
     * @param modelType имя модели из запроса
     * @return экземпляр солвера или {@code null}, если он не найден
     */
    public EconomicModelSolver getSolver(String modelType) {
        return solverMap.get(modelType);
    }
}