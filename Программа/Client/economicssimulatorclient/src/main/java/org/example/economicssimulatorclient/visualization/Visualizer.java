package org.example.economicssimulatorclient.visualization;

import org.example.economicssimulatorclient.dto.MathModelDto;
import org.example.economicssimulatorclient.dto.ComputationResultDto;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Visualizer {
    /**
     * Вернуть список доступных типов графиков для данной модели
     */
    List<String> getSupportedCharts(MathModelDto model);

    /**
     * Отобразить визуализацию результатов в заданном контейнере.
     * @param model   Математическая модель (DTO)
     * @param result  Результат вычисления (DTO)
     * @param container JavaFX Pane, куда добавлять график/элементы
     * @param chartType Тип графика ("График функции", "Тепловая карта" и т.д.)
     */
    void visualize(MathModelDto model, ComputationResultDto result, Pane container, String chartType);

    /**
     * Какой тип графика показывать по умолчанию (обычно первый из списка getSupportedCharts)
     */
    default String getDefaultChartType(MathModelDto model) {
        List<String> charts = getSupportedCharts(model);
        return charts.isEmpty() ? null : charts.get(0);
    }

    default double parseValueFromResult(ComputationResultDto result, String var) {
        if (result == null || result.resultData() == null) return Double.NaN;
        Pattern p = Pattern.compile(var + "\\s*=\\s*([-+]?\\d*\\.?\\d+)");
        Matcher m = p.matcher(result.resultData());
        if (m.find()) {
            try { return Double.parseDouble(m.group(1)); } catch (Exception ignored) {}
        }
        return Double.NaN;
    }
}
