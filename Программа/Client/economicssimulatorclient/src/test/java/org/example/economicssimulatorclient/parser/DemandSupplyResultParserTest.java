package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemandSupplyResultParserTest {


    @Test
    void returnsNoDataIfNoSupplyDemand() {
        var parser = new DemandSupplyResultParser();
        String json = "{\"something_else\":{}}";
        String result = parser.parse(json);
        assertThat(result).contains(I18n.t("result.demand.no_data"));
    }

    @Test
    void parsesEquilibriumAndExamples() {
        var parser = new DemandSupplyResultParser();
        String json = """
            {
              "supply_demand": {
                "equilibrium": {"quantity": 10, "price": 20},
                "demand": [{"quantity": 5, "price": 8}],
                "supply": [{"quantity": 7, "price": 12}]
              }
            }
            """;
        String result = parser.parse(json);
        assertThat(result).contains(I18n.t("result.demand.title"));
        assertThat(result).contains("10").contains("20");
    }
}
