package org.example.economicssimulatorclient.chart;

/**
 * Фабрика, выдающая нужный {@link ChartDrawer} для определённой экономической модели.
 */
public class ChartDrawerFactory {

    /**
     * Возвращает построитель графиков по названию модели.
     *
     * @param modelType строковый идентификатор модели
     * @return подходящий {@link ChartDrawer} или {@code null}, если тип не поддерживается
     */
    public static ChartDrawer getDrawer(String modelType) {
        return switch (modelType) {
            case "DemandSupply" -> new DemandSupplyChartBuilder();
            case "Elasticity" -> new ElasticityChartBuilder();
            case "CompetitionVsMonopoly" -> new CompetitionChartBuilder();
            case "ConsumerChoice" -> new ConsumerChoiceChartBuilder();
            case "ISLM" -> new ISLMChartBuilder();
            case "SolowGrowth" -> new SolowGrowthChartBuilder();
            case "PhillipsCurve" -> new PhillipsCurveChartBuilder();
            case "ADAS" -> new ADASChartBuilder();
            case "CAPM" -> new CAPMChartBuilder();
            case "BlackScholes" -> new BlackScholesChartBuilder();
            default -> null;
        };
    }
}
