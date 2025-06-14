package org.example.economicssimulatorclient.chart;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChartDrawerFactoryTest {

    @Test
    void getDrawer_returnsDrawerForKnownModelTypes() {
        assertThat(ChartDrawerFactory.getDrawer("DemandSupply")).isInstanceOf(DemandSupplyChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("Elasticity")).isInstanceOf(ElasticityChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("CompetitionVsMonopoly")).isInstanceOf(CompetitionChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("ConsumerChoice")).isInstanceOf(ConsumerChoiceChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("ISLM")).isInstanceOf(ISLMChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("SolowGrowth")).isInstanceOf(SolowGrowthChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("PhillipsCurve")).isInstanceOf(PhillipsCurveChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("ADAS")).isInstanceOf(ADASChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("CAPM")).isInstanceOf(CAPMChartBuilder.class);
        assertThat(ChartDrawerFactory.getDrawer("BlackScholes")).isInstanceOf(BlackScholesChartBuilder.class);
    }

    @Test
    void getDrawer_returnsNullForUnknownModelType() {
        assertThat(ChartDrawerFactory.getDrawer("UnknownType")).isNull();
        assertThat(ChartDrawerFactory.getDrawer("")).isNull();
        assertThat(ChartDrawerFactory.getDrawer(null)).isNull();
    }
}
