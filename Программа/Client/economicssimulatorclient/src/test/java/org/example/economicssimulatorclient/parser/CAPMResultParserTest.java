package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CAPMResultParserTest {

    @Test
    void returnsNoDataIfNothingPresent() {
        var parser = new CAPMResultParser();
        String json = "{}";
        String result = parser.parse(json);
        assertThat(result).contains("No CAPM data to display.");
    }

    @Test
    void parsesSMLFrontierAndDecomposition() {
        var parser = new CAPMResultParser();
        String json = """
        {
          "sml": { "sml": [ {"risk": 0.1, "return": 0.05}, {"risk": 0.9, "return": 0.25} ] },
          "efficient_frontier": { "portfolios": [ {"risk": 0.2, "return": 0.07}, {"risk": 0.5, "return": 0.14} ] },
          "decomposition": { "decomposition": [ {"label": "A", "alpha": 0.1, "beta": 0.2} ] }
        }
        """;
        String result = parser.parse(json);
        assertThat(result).contains("Security Market Line").contains("Efficient frontier").contains("decomposition");
    }
}
