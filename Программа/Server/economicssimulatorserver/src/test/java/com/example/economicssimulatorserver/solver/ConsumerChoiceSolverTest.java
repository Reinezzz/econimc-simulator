package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsumerChoiceSolverTest {

    private final ConsumerChoiceSolver solver = new ConsumerChoiceSolver();

    @Test
    void getModelType_returnsConsumerChoice() {
        assertThat(solver.getModelType()).isEqualTo("ConsumerChoice");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                10L, "ConsumerChoice",
                List.of(
                        new ModelParameterDto(1L, 10L, "I", "double", "100", "Доход", null, null),
                        new ModelParameterDto(2L, 10L, "Px", "double", "10", "Цена X", null, null),
                        new ModelParameterDto(3L, 10L, "Py", "double", "5", "Цена Y", null, null),
                        new ModelParameterDto(4L, 10L, "alpha", "double", "0.6", "Альфа", null, null),
                        new ModelParameterDto(5L, 10L, "U", "double", "20", "Полезность", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);

        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("indifference_curves");
        assertThat(resp.updatedParameters()).hasSize(5);
    }
}
