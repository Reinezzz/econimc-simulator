package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CompetitionResultParserTest {
    @Test
    void parsesCompetitionMonopolyAndDeadweight() {
        var parser = new CompetitionResultParser();
        String json = """
        {
          "profit_curves": {
            "competition": [ {"profit": 1, "quantity": 50}, {"profit": 5, "quantity": 100} ],
            "monopoly": [ {"profit": 8, "quantity": 80} ]
          },
          "comparison_hist": {
            "categories": [ "output" ],
            "competition": [110],
            "monopoly": [90]
          },
          "deadweight_area": {
            "monopolyQ": 80,
            "competitionQ": 100,
            "demand": [ {"price": 10} ],
            "supply": [ {"price": 8} ]
          }
        }
        """;
        String result = parser.parse(json);
        // Правильно: ищем видимые пользователю строки:
        assertThat(result).contains("Profit curves");
        assertThat(result).contains("Competition vs Monopoly");
        assertThat(result).contains("Deadweight loss area");
        assertThat(result).contains("monopoly");
        assertThat(result).contains("competition");
        // или если тест на русском, подставь русские ключи!
    }
}
