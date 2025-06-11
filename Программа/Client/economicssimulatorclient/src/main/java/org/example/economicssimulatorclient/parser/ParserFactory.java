package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;

public class ParserFactory {

    public static ResultParser getParser(String modelType) {
        if (modelType == null) {
            return new DefaultResultParser();
        }
        switch (modelType) {
            case "DemandSupply":
                return new DemandSupplyResultParser();
            case "Elasticity":
                return new ElasticityResultParser();
            case "CompetitionVsMonopoly":
                return new CompetitionResultParser();
            case "ConsumerChoice":
                return new ConsumerChoiceResultParser();
            case "ISLM":
                return new ISLMResultParser();
            case "SolowGrowth":
                return new SolowGrowthResultParser();
            case "PhillipsCurve":
                return new PhillipsCurveResultParser();
            case "ADAS":
                return new ADASResultParser();
            case "CAPM":
                return new CAPMResultParser();
            case "BlackScholes":
                return new BlackScholesResultParser();
            default:
                return new DefaultResultParser();
        }
    }

    private static class DefaultResultParser implements ResultParser {
        @Override
        public String parse(String json) {
            return I18n.t("result.unknown") + "\n" + json;
        }
    }
}
