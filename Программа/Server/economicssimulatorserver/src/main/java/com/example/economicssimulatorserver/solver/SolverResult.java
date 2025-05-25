package com.example.economicssimulatorserver.solver;

/**
 * Результат вычислений, возвращаемый solver-ом.
 *
 * @param resultJson сериализованный результат (обычно JSON)
 */

public record SolverResult(String resultJson) {}
