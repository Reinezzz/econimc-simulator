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
 * Солвер для модели оценки капитальных активов (CAPM).
 * <p>
 * Формирует расчёты ожидаемой доходности и визуализации,
 * такие как линия рынка ценных бумаг и эффективная граница.
 */
@Component
public class CAPMSolver implements EconomicModelSolver {

    /**
     * Рассчитывает показатели CAPM по заданным параметрам.
     *
     * @param request параметры рынка и характеристики портфеля
     * @return ответ с данными для графиков
     */
    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double Rf    = (Double) ParameterTypeConverter.fromString(paramMap.get("Rf"),    "double");
        double Rm    = (Double) ParameterTypeConverter.fromString(paramMap.get("Rm"),    "double");
        double beta  = (Double) ParameterTypeConverter.fromString(paramMap.get("beta"),  "double");
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double");
        double sigma = (Double) ParameterTypeConverter.fromString(paramMap.get("sigma"), "double");

        double expectedReturn = Rf + beta * (Rm - Rf) + alpha;

        List<Map<String, Number>> smlPoints = new ArrayList<>();
        smlPoints.add(Map.of("risk", 0.0, "return", Rf));
        smlPoints.add(Map.of("risk", beta, "return", expectedReturn));
        Map<String, Object> smlData = new LinkedHashMap<>();
        smlData.put("sml", smlPoints);

        Map<String, Object> efData = new LinkedHashMap<>();
        List<Map<String, Number>> portfolios = new ArrayList<>();
        for (double b = 0.0; b <= 2.0; b += 0.5) {
            double r = Rf + b * (Rm - Rf) + alpha;
            double s = b;
            portfolios.add(Map.of("risk", s, "return", r));
        }
        efData.put("portfolios", portfolios);
        List<Map<String, Number>> frontierPoints = new ArrayList<>();
        for (double b = 0.0; b <= 2.0; b += 0.2) {
            double r_i = Rf + b * (Rm - Rf);
            frontierPoints.add(Map.of("risk", b, "return", r_i));
        }
        efData.put("frontier", frontierPoints);

        List<Map<String, Object>> decompositionList = new ArrayList<>();
        decompositionList.add(Map.of(
                "label", "Portfolio",
                "alpha", alpha,
                "beta",  beta
        ));
        decompositionList.add(Map.of(
                "label", "Market",
                "alpha", 0.0,
                "beta",  1.0
        ));
        decompositionList.add(Map.of(
                "label", "Risk-free",
                "alpha", 0.0,
                "beta",  0.0
        ));
        Map<String, Object> decompData = new LinkedHashMap<>();
        decompData.put("decomposition", decompositionList);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("sml",                smlData);
        allCharts.put("efficient_frontier", efData);
        allCharts.put("decomposition",      decompData);
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
     * @return идентификатор модели CAPM
     */
    @Override
    public String getModelType() {
        return "CAPM";
    }
}
