package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.dto.ModelResultDto;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChartDataConverterTest {

    @Test
    void parseRawChartData_returnsEmptyMapOnNullOrInvalid() {
        assertThat(ChartDataConverter.parseRawChartData(null)).isEmpty();

        ModelResultDto result = new ModelResultDto(1L, 1L, "test", null, "now");
        assertThat(ChartDataConverter.parseRawChartData(result)).isEmpty();

        ModelResultDto badJson = new ModelResultDto(1L, 1L, "test", "not-a-json", "now");
        assertThat(ChartDataConverter.parseRawChartData(badJson)).isEmpty();
    }

    @Test
    void parseRawChartData_parsesValidJsonObject() {
        // {"chart1":{"a":1,"b":2},"chart2":42}
        String json = """
            {"chart1":{"a":1,"b":2},"chart2":42}
            """;
        ModelResultDto result = new ModelResultDto(1L, 1L, "test", json, "now");
        Map<String, Map<String, Object>> map = ChartDataConverter.parseRawChartData(result);
        assertThat(map).containsOnlyKeys("chart1", "chart2");
        assertThat(map.get("chart1")).containsEntry("a", 1).containsEntry("b", 2);
        assertThat(map.get("chart2")).containsEntry("value", 42);
    }

    @Test
    void parseEquilibrium_returnsMapWithEquilibriumOrOptimum() {
        String jsonEq = """
            {"equilibrium":{"x":1.5,"y":2.5},"chart":{}}
            """;
        ModelResultDto result = new ModelResultDto(1L, 1L, "test", jsonEq, "now");
        Map<String, Double> eq = ChartDataConverter.parseEquilibrium(result);
        assertThat(eq).containsEntry("x", 1.5).containsEntry("y", 2.5);

        String jsonOpt = """
            {"optimum":{"a":7.0}}
            """;
        result = new ModelResultDto(1L, 1L, "test", jsonOpt, "now");
        eq = ChartDataConverter.parseEquilibrium(result);
        assertThat(eq).containsEntry("a", 7.0);

        String jsonNone = "{\"some\":\"data\"}";
        result = new ModelResultDto(1L, 1L, "test", jsonNone, "now");
        eq = ChartDataConverter.parseEquilibrium(result);
        assertThat(eq).isEmpty();
    }
}
