package org.example.economicssimulatorclient.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParserFactoryTest {

    @Test
    void returnsProperParserForKnownTypes() {
        assertThat(ParserFactory.getParser("DemandSupply")).isInstanceOf(DemandSupplyResultParser.class);
        assertThat(ParserFactory.getParser("Elasticity")).isInstanceOf(ElasticityResultParser.class);
        assertThat(ParserFactory.getParser("CompetitionVsMonopoly")).isInstanceOf(CompetitionResultParser.class);
        assertThat(ParserFactory.getParser("ConsumerChoice")).isInstanceOf(ConsumerChoiceResultParser.class);
        assertThat(ParserFactory.getParser("ISLM")).isInstanceOf(ISLMResultParser.class);
        assertThat(ParserFactory.getParser("SolowGrowth")).isInstanceOf(SolowGrowthResultParser.class);
        assertThat(ParserFactory.getParser("PhillipsCurve")).isInstanceOf(PhillipsCurveResultParser.class);
        assertThat(ParserFactory.getParser("ADAS")).isInstanceOf(ADASResultParser.class);
        assertThat(ParserFactory.getParser("CAPM")).isInstanceOf(CAPMResultParser.class);
        assertThat(ParserFactory.getParser("BlackScholes")).isInstanceOf(BlackScholesResultParser.class);
    }

    @Test
    void returnsDefaultParserForUnknownOrNullType() {
        assertThat(ParserFactory.getParser("UnknownType").getClass().getSimpleName()).isEqualTo("DefaultResultParser");
        assertThat(ParserFactory.getParser(null).getClass().getSimpleName()).isEqualTo("DefaultResultParser");
    }
}
