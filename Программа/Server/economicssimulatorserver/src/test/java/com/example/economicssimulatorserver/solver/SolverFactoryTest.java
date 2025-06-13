package com.example.economicssimulatorserver.solver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SolverFactoryTest {

    @Test
    void getSolver_returnsCorrectSolver() {
        EconomicModelSolver capm = new CAPMSolver();
        EconomicModelSolver adas = new ADASSolver();

        SolverFactory factory = new SolverFactory(List.of(capm, adas));
        assertThat(factory.getSolver("CAPM")).isSameAs(capm);
        assertThat(factory.getSolver("ADAS")).isSameAs(adas);
        assertThat(factory.getSolver("Fake")).isNull();
    }
}
