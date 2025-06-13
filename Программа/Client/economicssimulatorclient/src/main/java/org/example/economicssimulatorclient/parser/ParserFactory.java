package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;

/**
 * Фабрика, выбирающая нужный {@link ResultParser} по типу модели.
 * Если тип неизвестен или равен {@code null}, используется
 * простой парсер по умолчанию, выводящий полученный JSON.
 */
public class ParserFactory {

    /**
     * Возвращает парсер, соответствующий указанному типу модели.
     *
     * @param modelType идентификатор модели
     * @return подходящая реализация {@link ResultParser}
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
     * Запасной парсер, используемый при неизвестном типе модели. Возвращает
     * метку "unknown" и переданный JSON без изменений.
     */
    private static class DefaultResultParser implements ResultParser {

        /**
         * {@inheritDoc}
         */
        @Override
        public String parse(String json) {
            return I18n.t("result.unknown") + "\n" + json;
        }
    }
}
