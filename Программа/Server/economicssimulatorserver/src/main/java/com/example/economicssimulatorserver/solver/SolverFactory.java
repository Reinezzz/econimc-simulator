package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.MathModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Фабрика для выбора подходящего Solver по модели.
 */
@Component
public class SolverFactory {

    private final List<Solver> solvers;

    /**
     * Все реализованные Solver-ы внедряются через DI (Spring).
     */
    public SolverFactory(List<Solver> solvers) {
        this.solvers = List.copyOf(solvers); // иммутабельность
    }

    /**
     * Возвращает подходящий Solver для данной модели.
     * @param model Экземпляр модели.
     * @return Solver
     * @throws LocalizedException если подходящий Solver не найден.
     */
    public Solver getSolver(MathModel model) throws LocalizedException {
        return solvers.stream()
                .filter(solver -> solver.supports(model))
                .findFirst()
                .orElseThrow(() ->
                        new LocalizedException("No suitable solver found for model type: " + model.getModelType()));
    }
}
