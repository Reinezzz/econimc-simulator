package org.example.economicssimulatorclient.chart;

public class ChartDrawerFactory {

    public static ChartDrawer getDrawer(String modelType) {
        return switch (modelType) {
            case "DemandSupply"       -> new DemandSupplyChartBuilder();
            case "Elasticity"         -> new ElasticityChartBuilder();
            case "CompetitionVsMonopoly"        -> new CompetitionChartBuilder();
            case "ConsumerChoice"     -> new ConsumerChoiceChartBuilder();
            case "ISLM"               -> new ISLMChartBuilder();
            case "SolowGrowth"        -> new SolowGrowthChartBuilder();
            case "PhillipsCurve"      -> new PhillipsCurveChartBuilder();
            case "ADAS"               -> new ADASChartBuilder();
            case "CAPM"               -> new CAPMChartBuilder();
            case "BlackScholes"       -> new BlackScholesChartBuilder();
            default                   -> null; // или можно бросить исключение, если не найден
        };
    }
}
