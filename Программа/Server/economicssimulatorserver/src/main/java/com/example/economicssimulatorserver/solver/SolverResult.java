package com.example.economicssimulatorserver.solver;

import lombok.Getter;
import lombok.Setter;

/**
 * Результат вычислений, возвращаемый solver-ом.
 *
 * @param resultJson сериализованный результат (обычно JSON)
 */
public record SolverResult(String resultJson) {}
