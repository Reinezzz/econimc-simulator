package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ADASSolverTest {

    private final ADASSolver solver = new ADASSolver();

    @Test
    void getModelType_returnsADAS() {
        assertThat(solver.getModelType()).isEqualTo("ADAS");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                1L,
                "ADAS",
                List.of(
                        new ModelParameterDto(1L, 1L, "Y_pot", "double", "1000", "Potential Y", null, null),
                        new ModelParameterDto(2L, 1L, "P0", "double", "100", "Initial Price", null, null),
                        new ModelParameterDto(3L, 1L, "AD_slope", "double", "-3", "AD Slope", null, null),
                        new ModelParameterDto(4L, 1L, "AS_slope", "double", "2", "AS Slope", null, null),
                        new ModelParameterDto(5L, 1L, "shock", "double", "50", "Shock", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);
        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("equilibrium");
        assertThat(resp.updatedParameters()).hasSize(5);
    }
}
