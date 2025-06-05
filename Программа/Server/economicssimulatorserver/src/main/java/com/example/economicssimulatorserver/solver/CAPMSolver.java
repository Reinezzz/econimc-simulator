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
 * Обновлённый солвер для модели CAPM, который подготавливает три блока:
 * 1) "sml"               — Line Market Line;
 * 2) "efficient_frontier" — scatter + line для эффективной границы;
 * 3) "decomposition"      — BarChart для альфа/бета.
 */
@Component
public class CAPMSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Считываем входные параметры
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double Rf    = (Double) ParameterTypeConverter.fromString(paramMap.get("Rf"),    "double"); // безрисковая ставка
        double Rm    = (Double) ParameterTypeConverter.fromString(paramMap.get("Rm"),    "double"); // доходность рынка
        double beta  = (Double) ParameterTypeConverter.fromString(paramMap.get("beta"),  "double"); // β портфеля
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double"); // α портфеля
        double sigma = (Double) ParameterTypeConverter.fromString(paramMap.get("sigma"), "double"); // риск (σ) портфеля

        // 2) Расчёт ожидаемой доходности портфеля по CAPM:
        //    E(R) = Rf + β * (Rm - Rf) + α
        double expectedReturn = Rf + beta * (Rm - Rf) + alpha;

        // === 3) БЛОК "sml" ===
        // Чтобы нарисовать SML, нам нужно две точки:
        //   β=0  → доходность=Rf
        //   β=β  → доходность=expectedReturn
        List<Map<String, Number>> smlPoints = new ArrayList<>();
        smlPoints.add(Map.of("risk", 0.0, "return", Rf));
        smlPoints.add(Map.of("risk", beta, "return", expectedReturn));
        Map<String, Object> smlData = new LinkedHashMap<>();
        smlData.put("sml", smlPoints);

        // === 4) БЛОК "efficient_frontier" ===
        // Несколько портфелей и "frontier"
        Map<String, Object> efData = new LinkedHashMap<>();
        List<Map<String, Number>> portfolios = new ArrayList<>();
        // Добавим 5 портфелей с разными beta (и sigma = beta для примера)
        for (double b = 0.0; b <= 2.0; b += 0.5) {
            double r = Rf + b * (Rm - Rf) + alpha;
            double s = b; // sigma = beta для примера, если нужна другая формула - подставить свою
            portfolios.add(Map.of("risk", s, "return", r));
        }
        efData.put("portfolios", portfolios);
        // Эффективная граница
        List<Map<String, Number>> frontierPoints = new ArrayList<>();
        for (double b = 0.0; b <= 2.0; b += 0.2) {
            double r_i = Rf + b * (Rm - Rf);
            frontierPoints.add(Map.of("risk", b, "return", r_i));
        }
        efData.put("frontier", frontierPoints);

        // === 5) БЛОК "decomposition" ===
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

        // === 6) Собираем финальный allCharts ===
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("sml",                smlData);
        allCharts.put("efficient_frontier", efData);
        allCharts.put("decomposition",      decompData);

        // === 7) Сериализуем и возвращаем ===
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
        return "CAPM";
    }
}
