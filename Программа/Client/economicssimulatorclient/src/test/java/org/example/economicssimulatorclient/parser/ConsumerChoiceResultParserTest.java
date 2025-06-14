package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ConsumerChoiceResultParserTest {
    @Test
    void parsesIndifferenceCurves() {
        var parser = new ConsumerChoiceResultParser();
        String json = """
        {
            "indifference_curves": {
                "indifference_curves": [
                    [ {"quantity": 1, "price": 10}, {"quantity": 5, "price": 15} ]
                ],
                "budget": [
                    {"quantity": 1, "price": 9},
                    {"quantity": 5, "price": 18}
                ]
            },
            "optimum_map": {
                "optimum": { "x": 3, "y": 14 }
            }
        }
        """;
        String result = parser.parse(json);
        assertThat(result).contains("Indifference curves");
        assertThat(result).contains("Budget constraint"); // вместо "budget"
        assertThat(result).contains("Utility maximum point"); // или "Optimal"
    }
}
