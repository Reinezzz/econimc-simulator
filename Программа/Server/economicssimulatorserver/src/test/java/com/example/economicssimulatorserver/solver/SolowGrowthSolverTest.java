package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SolowGrowthSolverTest {

    private final SolowGrowthSolver solver = new SolowGrowthSolver();

    @Test
    void getModelType_returnsSolowGrowth() {
        assertThat(solver.getModelType()).isEqualTo("SolowGrowth");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                15L, "SolowGrowth",
                List.of(
                        new ModelParameterDto(1L, 15L, "s", "double", "0.2", "Ставка сбережений", null, null),
                        new ModelParameterDto(2L, 15L, "n", "double", "0.02", "Прирост населения", null, null),
                        new ModelParameterDto(3L, 15L, "delta", "double", "0.05", "Амортизация", null, null),
                        new ModelParameterDto(4L, 15L, "alpha", "double", "0.3", "Доля капитала", null, null),
                        new ModelParameterDto(5L, 15L, "A", "double", "2", "Технология", null, null),
                        new ModelParameterDto(6L, 15L, "K0", "double", "50", "Начальный капитал", null, null),
                        new ModelParameterDto(7L, 15L, "L0", "double", "20", "Начальная рабочая сила", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);

        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("trajectories");
        assertThat(resp.updatedParameters()).hasSize(7);
    }
}
