package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemandSupplySolverTest {

    private final DemandSupplySolver solver = new DemandSupplySolver();

    @Test
    void getModelType_returnsDemandSupply() {
        assertThat(solver.getModelType()).isEqualTo("DemandSupply");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                11L, "DemandSupply",
                List.of(
                        new ModelParameterDto(1L, 11L, "a", "double", "100", "a", null, null),
                        new ModelParameterDto(2L, 11L, "b", "double", "2", "b", null, null),
                        new ModelParameterDto(3L, 11L, "c", "double", "10", "c", null, null),
                        new ModelParameterDto(4L, 11L, "d", "double", "1", "d", null, null),
                        new ModelParameterDto(5L, 11L, "P_min", "double", "0", "Pmin", null, null),
                        new ModelParameterDto(6L, 11L, "P_max", "double", "60", "Pmax", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);

        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("supply_demand");
        assertThat(resp.updatedParameters()).hasSize(6);
    }
}
