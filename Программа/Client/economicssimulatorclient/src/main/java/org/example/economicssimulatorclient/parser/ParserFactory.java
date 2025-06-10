package org.example.economicssimulatorclient.parser;

/**
 * Фабрика парсеров результатов экономических моделей.
 */
public class ParserFactory {

    /**
     * Возвращает парсер по типу экономической модели.
     * @param modelType Тип модели (например, "DemandSupply", "Elasticity" и т.д.)
     * @return Реализация ResultParser для данного типа модели.
     */
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

    /**
     * Стандартный парсер на случай неизвестного типа модели.
     */
    private static class DefaultResultParser implements ResultParser {
        @Override
        public String parse(String json) {
            return "Результат (неизвестный тип модели):\n" + json;
        }
    }
}
