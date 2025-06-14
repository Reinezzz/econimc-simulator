package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PhillipsCurveResultParserTest {
    @Test
    void parsesScatterAndTimeseries() {
        var parser = new PhillipsCurveResultParser();
        String json = """
            {
                "scatter": { "points": [ {"inflation": 1, "unemployment": 5}, {"inflation": 3, "unemployment": 4} ] },
                "timeseries": { "inflation": [ {"inflation": 1}, {"inflation": 2} ], "unemployment": [ {"unemployment": 6}, {"unemployment": 3} ] }
            }
        """;
        String result = parser.parse(json);
        assertThat(result).contains("inflation").contains("unemployment");
    }
}
