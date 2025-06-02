package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.util.ParameterTypeConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Обновлённый солвер для модели «Эластичность спроса», который формирует:
 * 1. "elasticity_curves"   — набор точек для двух серий ("elastic" и "inelastic");
 * 2. "revenue_bars"        — списки цен и выручки для столбчатой диаграммы;
 * 3. "elasticity_heatmap"  — списки категорий и значений эластичности для тепловой карты.
 */
@Component
public class ElasticitySolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Считаем входные параметры
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double Q0       = (Double) ParameterTypeConverter.fromString(paramMap.get("Q0"), "double");
        double P0       = (Double) ParameterTypeConverter.fromString(paramMap.get("P0"), "double");
        double Q1       = (Double) ParameterTypeConverter.fromString(paramMap.get("Q1"), "double");
        double P1       = (Double) ParameterTypeConverter.fromString(paramMap.get("P1"), "double");
        String category = paramMap.getOrDefault("category", "Обычный товар");

        // Рассчитываем коэффициент эластичности по дуге:
        // ((Q1 - Q0)/((Q1+Q0)/2)) / ((P1 - P0)/((P1+P0)/2))
        double elasticity = ((Q1 - Q0) / ((Q1 + Q0) / 2.0))
                / ((P1 - P0) / ((P1 + P0) / 2.0));

        // Выручка до и после
        double revenue0 = Q0 * P0;
        double revenue1 = Q1 * P1;

        // === 2) Формируем три блока данных ===

        // --- 2.1. Блок "elasticity_curves" ---
        // Для минимального работоспособного отображения дадим две точки:
        //   (P0, Q0)  и  (P1, Q1)  и дублируем их в обе серии.
        List<Map<String, Number>> curvePoints = new ArrayList<>();
        curvePoints.add(Map.of("price", P0, "quantity", Q0));
        curvePoints.add(Map.of("price", P1, "quantity", Q1));

        Map<String, Object> curvesData = new LinkedHashMap<>();
        // "elastic" и "inelastic" пока совпадают
        curvesData.put("elastic",   curvePoints);
        curvesData.put("inelastic", curvePoints);

        // --- 2.2. Блок "revenue_bars" ---
        // Клиентский buildRevenueBarChart ждёт:
        //    "prices": List<Number>
        //    "revenue": List<Number>
        Map<String, Object> revenueData = new LinkedHashMap<>();
        revenueData.put("prices",  List.of(P0, P1));
        revenueData.put("revenue", List.of(revenue0, revenue1));

        // --- 2.3. Блок "elasticity_heatmap" ---
        // Клиентский buildElasticityHeatmap ждёт:
        //    "categories": List<String>
        //    "elasticity": List<Number>
        Map<String, Object> heatmapData = new LinkedHashMap<>();
        heatmapData.put("categories",  List.of(category));
        heatmapData.put("elasticity",  List.of(elasticity));

        // === 3) Собираем «объединённый» результат ===
        // Топ-уровень: три вложенных Map под ключами, соответствующими case-ветвям в ElasticityChartBuilder.
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("elasticity_curves",   curvesData);
        allCharts.put("revenue_bars",        revenueData);
        allCharts.put("elasticity_heatmap",  heatmapData);

        // === 4) Упаковываем в DTO ===
        ModelResultDto result = new ModelResultDto(
                null,
                request.modelId(),
                "chart",
                ParameterTypeConverter.toString(allCharts, "json"),
                null
        );
        return new CalculationResponseDto(result, request.parameters());
    }

    @Override
    public String getModelType() {
        return "Elasticity";
    }
}
