package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionSolverTest {

    private final CompetitionSolver solver = new CompetitionSolver();

    @Test
    void getModelType_returnsCompetitionVsMonopoly() {
        assertThat(solver.getModelType()).isEqualTo("CompetitionVsMonopoly");
    }

    @Test
    void solve_returnsValidResult() {
        CalculationRequestDto req = new CalculationRequestDto(
                4L,
                "CompetitionVsMonopoly",
                List.of(
                        new ModelParameterDto(1L, 4L, "a", "double", "100", "a", null, null),
                        new ModelParameterDto(2L, 4L, "b", "double", "2", "b", null, null),
                        new ModelParameterDto(3L, 4L, "c", "double", "10", "c", null, null),
                        new ModelParameterDto(4L, 4L, "d", "double", "1", "d", null, null),
                        new ModelParameterDto(5L, 4L, "MC", "double", "10", "MC", null, null),
                        new ModelParameterDto(6L, 4L, "FC", "double", "50", "FC", null, null),
                        new ModelParameterDto(7L, 4L, "market_type", "string", "competition", "MarketType", null, null)
                )
        );
        CalculationResponseDto resp = solver.solve(req);
        assertThat(resp).isNotNull();
        assertThat(resp.result()).isNotNull();
        assertThat(resp.result().resultType()).isEqualTo("chart");
        assertThat(resp.result().resultData()).contains("profit_curves");
        assertThat(resp.updatedParameters()).hasSize(7);
    }
}
