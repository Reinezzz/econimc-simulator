package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;

import java.util.Map;

public interface ChartDrawer {

    Node buildChart(String chartKey, Map<String, Object> chartData);
}
