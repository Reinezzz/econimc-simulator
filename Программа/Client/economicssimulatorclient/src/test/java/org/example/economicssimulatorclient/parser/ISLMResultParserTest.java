package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ISLMResultParserTest {
    @Test
    void parsesEquilibrium() {
        var parser = new ISLMResultParser();
        String json = """
            { "is_lm": { "equilibrium": {"rate": 3.2, "income": 2000}, "IS": [ {"rate": 3, "income": 1500}, {"rate": 2, "income": 2500} ], "LM": [ {"rate": 3, "income": 1200}, {"rate": 2, "income": 2700} ] } }
        """;
        String result = parser.parse(json);
        assertThat(result).contains("equilibrium").contains("rate").contains("income");
    }
}
