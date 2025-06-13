package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BlackScholesSolverTest {

    private final BlackScholesSolver solver = new BlackScholesSolver();

    @Test
    void getModelType_returnsBlackScholes() {
        assertThat(solver.getModelType()).isEqualTo("BlackScholes");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                2L,
                "BlackScholes",
                List.of(
                        new ModelParameterDto(1L, 2L, "S", "double", "100", "Spot", null, null),
                        new ModelParameterDto(2L, 2L, "K", "double", "100", "Strike", null, null),
                        new ModelParameterDto(3L, 2L, "T", "double", "1", "Time", null, null),
                        new ModelParameterDto(4L, 2L, "r", "double", "0.05", "Rate", null, null),
                        new ModelParameterDto(5L, 2L, "sigma", "double", "0.2", "Volatility", null, null),
                        new ModelParameterDto(6L, 2L, "option_type", "string", "call", "Type", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);
        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("surface");
        assertThat(resp.updatedParameters()).hasSize(6);
    }
}
