package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CAPMSolverTest {

    private final CAPMSolver solver = new CAPMSolver();

    @Test
    void getModelType_returnsCAPM() {
        assertThat(solver.getModelType()).isEqualTo("CAPM");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                3L,
                "CAPM",
                List.of(
                        new ModelParameterDto(1L, 3L, "Rf", "double", "0.01", "Rf", null, null),
                        new ModelParameterDto(2L, 3L, "Rm", "double", "0.09", "Rm", null, null),
                        new ModelParameterDto(3L, 3L, "beta", "double", "1.5", "Beta", null, null),
                        new ModelParameterDto(4L, 3L, "alpha", "double", "0.01", "Alpha", null, null),
                        new ModelParameterDto(5L, 3L, "sigma", "double", "0.25", "Sigma", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);
        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("efficient_frontier");
        assertThat(resp.updatedParameters()).hasSize(5);
    }
}
