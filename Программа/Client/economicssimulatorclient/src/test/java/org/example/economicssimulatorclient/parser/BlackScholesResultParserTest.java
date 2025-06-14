package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BlackScholesResultParserTest {

    @Test
    void returnsNoDataIfEmpty() {
        var parser = new BlackScholesResultParser();
        String result = parser.parse("{}");
        // Актуальный текст зависит от локали, вот что возвращает en:
        assertThat(result).contains("No Black-Scholes data to display.");
    }

    @Test
    void parsesSurfaceDecayAndGreeks() {
        var parser = new BlackScholesResultParser();
        String json = """
        {
          "surface": { "surface": [ [ {"S": 10, "C": 5}, {"S": 20, "C": 10} ] ] },
          "decay": { "decay": [ {"T": 10, "C": 5}, {"T": 1, "C": 2} ] },
          "greeks": { "greeks": { "delta": [ {"value": 0.7}, {"value": 0.9} ] } }
        }
        """;
        String result = parser.parse(json);
        // Проверяем части, которые реально выводятся на текущей локали (en):
        assertThat(result).contains("Call option prices").contains("Effect of time to expiration").contains("Delta");
    }
}
