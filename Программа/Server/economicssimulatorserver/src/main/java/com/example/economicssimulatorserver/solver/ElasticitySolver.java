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
 * Солвер, вычисляющий ценовую эластичность спроса.
 * <p>
 * Строит тепловые карты и сравнивает выручку для набора товаров.
 */
@Component
public class ElasticitySolver implements EconomicModelSolver {

    /**
     * Рассчитывает эластичность по начальному и конечному значениям цены и количества.
     *
     * @param request параметры изменения цены и объёмов
     * @return ответ с показателями эластичности и визуализациями
     */
    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        List<String> categories = Arrays.stream(paramMap.getOrDefault("category", "Обычный товар")
                        .split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        int n = categories.size();

        List<Double> p0List = parseNumberList(paramMap.get("P0"), n);
        List<Double> p1List = parseNumberList(paramMap.get("P1"), n);
        List<Double> q0List = parseNumberList(paramMap.get("Q0"), n);
        List<Double> q1List = parseNumberList(paramMap.get("Q1"), n);

        List<Double> elasticities = new ArrayList<>();
        Map<String, Object> curvesData = new LinkedHashMap<>();
        List<Double> revenue0List = new ArrayList<>();
        List<Double> revenue1List = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double P0 = p0List.get(i);
            double P1 = p1List.get(i);
            double Q0 = q0List.get(i);
            double Q1 = q1List.get(i);

            double elasticity = ((Q1 - Q0) / ((Q1 + Q0) / 2.0))
                    / ((P1 - P0) / ((P1 + P0) / 2.0));
            elasticities.add(elasticity);

            List<Map<String, Number>> curvePoints = new ArrayList<>();
            curvePoints.add(Map.of("price", P0, "quantity", Q0));
            curvePoints.add(Map.of("price", P1, "quantity", Q1));
            String seriesName = i == 0 ? "elastic" : (i == 1 ? "inelastic" : "series" + (i + 1));
            curvesData.put(seriesName, curvePoints);

            revenue0List.add(P0 * Q0);
            revenue1List.add(P1 * Q1);
        }

        Map<String, Object> heatmapData = new LinkedHashMap<>();
        heatmapData.put("categories", categories);
        heatmapData.put("elasticity", elasticities);

        Map<String, Object> revenueData = new LinkedHashMap<>();
        revenueData.put("categories", categories);
        revenueData.put("revenue0", revenue0List);
        revenueData.put("revenue1", revenue1List);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("elasticity_curves", curvesData);
        allCharts.put("revenue_bars", revenueData);
        allCharts.put("elasticity_heatmap", heatmapData);

        ModelResultDto result = new ModelResultDto(
                null,
                request.modelId(),
                "chart",
                ParameterTypeConverter.toString(allCharts, "json"),
                null
        );
        return new CalculationResponseDto(result, request.parameters());
    }

    /**
     * @return идентификатор модели эластичности
     */
    @Override
    public String getModelType() {
        return "Elasticity";
    }

    /**
     * Разбирает список чисел, разделённых точкой с запятой.
     */
    private List<Double> parseNumberList(String input, int size) {
        if (input == null || input.isEmpty()) {
            List<Double> zeros = new ArrayList<>(Collections.nCopies(size, 0.0));
            return zeros;
        }
        String[] parts = input.split(";");
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (i < parts.length) {
                try {
                    result.add(Double.parseDouble(parts[i].trim()));
                } catch (NumberFormatException e) {
                    result.add(0.0);
                }
            } else {
                result.add(0.0);
            }
        }
        return result;
    }
}
