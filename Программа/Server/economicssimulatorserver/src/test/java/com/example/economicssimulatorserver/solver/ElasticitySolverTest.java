package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ElasticitySolverTest {

    private final ElasticitySolver solver = new ElasticitySolver();

    @Test
    void getModelType_returnsElasticity() {
        assertThat(solver.getModelType()).isEqualTo("Elasticity");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                12L, "Elasticity",
                List.of(
                        new ModelParameterDto(1L, 12L, "category", "string", "Товар1;Товар2", "Категории", null, null),
                        new ModelParameterDto(2L, 12L, "P0", "string", "10;15", "P0", null, null),
                        new ModelParameterDto(3L, 12L, "P1", "string", "12;17", "P1", null, null),
                        new ModelParameterDto(4L, 12L, "Q0", "string", "100;120", "Q0", null, null),
                        new ModelParameterDto(5L, 12L, "Q1", "string", "80;140", "Q1", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);

        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("elasticity_heatmap");
        assertThat(resp.updatedParameters()).hasSize(5);
    }
}
