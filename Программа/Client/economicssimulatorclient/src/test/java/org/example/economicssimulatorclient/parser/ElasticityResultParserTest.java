package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ElasticityResultParserTest {

    @Test
    void parsesBasicElasticityData() {
        var parser = new ElasticityResultParser();
        String json = """
        {
          "elasticity_curves": {
            "elastic": [
              {"quantity": 10, "price": 20},
              {"quantity": 15, "price": 17}
            ]
          },
          "revenue_bars": {
            "categories": ["A"],
            "revenue0": [100],
            "revenue1": [120]
          },
          "elasticity_heatmap": {
            "categories": ["A"],
            "elasticity": [0.7]
          }
        }
        """;
        String result = parser.parse(json);
        assertThat(result).contains("Revenue by category"); // вместо "revenue"
        assertThat(result).contains("Elasticity by category");
        assertThat(result).contains("elastic demand");
    }
}
