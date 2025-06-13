package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ISLMSolverTest {

    private final ISLMSolver solver = new ISLMSolver();

    @Test
    void getModelType_returnsISLM() {
        assertThat(solver.getModelType()).isEqualTo("ISLM");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                13L, "ISLM",
                List.of(
                        new ModelParameterDto(1L, 13L, "C0", "double", "20", "C0", null, null),
                        new ModelParameterDto(2L, 13L, "c1", "double", "0.8", "c1", null, null),
                        new ModelParameterDto(3L, 13L, "I0", "double", "15", "I0", null, null),
                        new ModelParameterDto(4L, 13L, "b", "double", "5", "b", null, null),
                        new ModelParameterDto(5L, 13L, "G", "double", "30", "G", null, null),
                        new ModelParameterDto(6L, 13L, "T", "double", "10", "T", null, null),
                        new ModelParameterDto(7L, 13L, "Ms", "double", "100", "Ms", null, null),
                        new ModelParameterDto(8L, 13L, "L0", "double", "20", "L0", null, null),
                        new ModelParameterDto(9L, 13L, "l1", "double", "0.4", "l1", null, null),
                        new ModelParameterDto(10L, 13L, "l2", "double", "0.5", "l2", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);

        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("is_lm");
        assertThat(resp.updatedParameters()).hasSize(10);
    }
}
