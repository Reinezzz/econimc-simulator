package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SolowGrowthResultParserTest {
    @Test
    void parsesTrajectoriesAndPhase() {
        var parser = new SolowGrowthResultParser();
        String json = """
            {
                "trajectories": { "capital": [ {"time": 1, "capital": 100}, {"time": 2, "capital": 200} ], "output": [ {"time": 1, "output": 20}, {"time": 2, "output": 25} ] },
                "phase": { "phase": [ {"capital": 100, "capital_change": 2}, {"capital": 200, "capital_change": 1} ] }
            }
        """;
        String result = parser.parse(json);
        assertThat(result).contains("capital").contains("output").contains("phase");
    }
}
