package org.example.economicssimulatorclient.chart;

import javafx.scene.Node;

import java.util.Map;

/**
 * Интерфейс построителя графиков экономических моделей.
 * Реализации преобразуют входные данные в JavaFX графики.
 */
public interface ChartDrawer {

    /**
     * Создает график в зависимости от заданного ключа.
     *
     * @param chartKey  строковый идентификатор типа графика
     * @param chartData набор данных, используемых для построения
     * @return узел JavaFX с готовой визуализацией
     */
    Node buildChart(String chartKey, Map<String, Object> chartData);
}
