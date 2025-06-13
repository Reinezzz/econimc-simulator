package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PhillipsCurveSolverTest {

    private final PhillipsCurveSolver solver = new PhillipsCurveSolver();

    @Test
    void getModelType_returnsPhillipsCurve() {
        assertThat(solver.getModelType()).isEqualTo("PhillipsCurve");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                14L, "PhillipsCurve",
                List.of(
                        new ModelParameterDto(1L, 14L, "pi0", "double", "0.05", "pi0", null, null),
                        new ModelParameterDto(2L, 14L, "u0", "double", "7", "u0", null, null),
                        new ModelParameterDto(3L, 14L, "alpha", "double", "0.6", "alpha", null, null),
                        new ModelParameterDto(4L, 14L, "u_n", "double", "6", "u_n", null, null),
                        new ModelParameterDto(5L, 14L, "pi_e", "double", "0.04", "pi_e", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);

        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("scatter");
        assertThat(resp.updatedParameters()).hasSize(5);
    }
}
