package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultResultParserTest {

    @Test
    void returnsUnknownPrefixAndJson() {
        var parser = ParserFactory.getParser("NonExistentType");
        String json = "{\"test\":\"abc\"}";
        String result = parser.parse(json);
        assertThat(result)
                .contains(I18n.t("result.unknown"))
                .contains(json);
    }
}
