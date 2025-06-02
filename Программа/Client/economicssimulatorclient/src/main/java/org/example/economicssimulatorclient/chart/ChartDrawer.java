package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;
import java.util.Map;

public interface ChartDrawer {
    /**
     * @param chartKey название вида графика внутри модели (например "supply_demand", "consumer_surplus")
     * @param chartData любые данные, полученные от сервера (обычно Map или объект, распаршенный из resultData)
     * @return JavaFX Node (график, диаграмма, 3D-поверхность и т.д.)
     */
    Node buildChart(String chartKey, Map<String, Object> chartData);
}
