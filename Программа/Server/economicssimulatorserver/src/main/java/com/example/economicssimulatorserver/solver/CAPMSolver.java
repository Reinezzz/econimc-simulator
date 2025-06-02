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
        smlPoints.add(Map.of("risk",   0.0,  "return", Rf));
        smlPoints.add(Map.of("risk",  beta, "return", expectedReturn));
        // Оборачиваем в Map<"sml"→List<…>>
        Map<String, Object> smlData = new LinkedHashMap<>();
        smlData.put("sml", smlPoints);

        // === 4) БЛОК "efficient_frontier" ===
        // Клиент ожидает Map со списками "portfolios" и "frontier"
        Map<String, Object> efData = new LinkedHashMap<>();
        // - "portfolios": точка (σ, expectedReturn)
        efData.put("portfolios", List.of(Map.of("risk", sigma, "return", expectedReturn)));
        // - "frontier": β от 0 до 2 шагом 0.2, E(R)=Rf + β*(Rm-Rf)
        List<Map<String, Number>> frontierPoints = new ArrayList<>();
        for (double b = 0.0; b <= 2.0; b += 0.2) {
            double r_i = Rf + b * (Rm - Rf);
            frontierPoints.add(Map.of("risk", b, "return", r_i));
        }
        efData.put("frontier", frontierPoints);

        // === 5) БЛОК "decomposition" ===
        // Клиент ожидает List<Map<"label",String; "alpha",Number; "beta",Number>>
        List<Map<String, Object>> decompositionList = new ArrayList<>();
        decompositionList.add(Map.of(
                "label", "Portfolio",
                "alpha", alpha,
                "beta",  beta
        ));
        // Оборачиваем в Map<"decomposition"→List<…>>
        Map<String, Object> decompData = new LinkedHashMap<>();
        decompData.put("decomposition", decompositionList);

        // === 6) Собираем финальный allCharts ===
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("sml",                smlData);      // Map с одним ключом "sml"→List<…>
        allCharts.put("efficient_frontier", efData);       // Map с ключами "portfolios", "frontier"
        allCharts.put("decomposition",      decompData);   // Map с одним ключом "decomposition"→List<…>

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
