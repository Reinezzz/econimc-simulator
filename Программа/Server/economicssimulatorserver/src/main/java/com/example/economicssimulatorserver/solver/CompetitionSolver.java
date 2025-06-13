package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.util.ParameterTypeConverter;
import com.example.economicssimulatorserver.util.ChartDataUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Солвер, сравнивающий результаты совершенной конкуренции и монополии.
 * <p>
 * Строит кривые прибыли и показатели благосостояния
 * при заданных параметрах рынка.
 */
@Component
public class CompetitionSolver implements EconomicModelSolver {

    /**
     * Вычисляет показатели рынка для сценариев конкуренции и монополии.
     *
     * @param request параметры спроса и издержек
     * @return ответ с данными для графиков
     */
    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));
        double a          = (Double) ParameterTypeConverter.fromString(paramMap.get("a"),  "double");
        double b          = (Double) ParameterTypeConverter.fromString(paramMap.get("b"),  "double");
        double c          = (Double) ParameterTypeConverter.fromString(paramMap.get("c"),  "double");
        double d          = (Double) ParameterTypeConverter.fromString(paramMap.get("d"),  "double");
        double MC         = (Double) ParameterTypeConverter.fromString(paramMap.get("MC"), "double");
        double FC         = (Double) ParameterTypeConverter.fromString(paramMap.get("FC"), "double");
        String marketType = paramMap.getOrDefault("market_type", "competition");

        double P_max = a / b * 1.2;
        double[] P   = ChartDataUtil.range(0, P_max, 40);

        double[] Qd = new double[P.length];
        double[] Qs = new double[P.length];
        for (int i = 0; i < P.length; i++) {
            Qd[i] = a - b * P[i];
            Qs[i] = c + d * P[i];
        }
        double eqPriceCompetition    = (a - c) / (b + d);
        double eqQuantityCompetition = c + d * eqPriceCompetition;
        double eqQuantityMonopoly = (a - MC) * b / 2.0;
        double eqPriceMonopoly    = (a - eqQuantityMonopoly) / b;

        double profitCompetition = (eqPriceCompetition - MC) * eqQuantityCompetition - FC;
        double profitMonopoly    = (eqPriceMonopoly    - MC) * eqQuantityMonopoly    - FC;

        List<Map<String, Number>> competitionCurve = new ArrayList<>();
        List<Map<String, Number>> monopolyCurve    = new ArrayList<>();

        for (int i = 0; i < P.length; i++) {
            double price = P[i];
            double qSup  = Qs[i];
            double qDem  = Qd[i];

            double profitC = (price - MC) * qSup - FC;
            competitionCurve.add(Map.of(
                    "quantity", qSup,
                    "profit",   profitC
            ));

            double profitM = (price - MC) * qDem - FC;
            monopolyCurve.add(Map.of(
                    "quantity", qDem,
                    "profit",   profitM
            ));
        }

        Map<String, Object> profitData = new LinkedHashMap<>();
        profitData.put("competition", competitionCurve);
        profitData.put("monopoly",    monopolyCurve);

        List<String> categories = List.of("Цена", "Количество", "Прибыль");
        List<Number> compValues  = List.of(eqPriceCompetition, eqQuantityCompetition, profitCompetition);
        List<Number> monoValues  = List.of(eqPriceMonopoly, eqQuantityMonopoly, profitMonopoly);

        Map<String, Object> histData = new LinkedHashMap<>();
        histData.put("categories",  categories);
        histData.put("competition",  compValues);
        histData.put("monopoly",     monoValues);

        Map<String, Object> dwlData = new LinkedHashMap<>();
        dwlData.put("demand",    ChartDataUtil.pointsList(P, Qd));
        dwlData.put("supply",    ChartDataUtil.pointsList(P, Qs));
        dwlData.put("monopolyQ", eqQuantityMonopoly);
        dwlData.put("competitionQ", eqQuantityCompetition);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("profit_curves",   profitData);
        allCharts.put("comparison_hist", histData);
        allCharts.put("deadweight_area", dwlData);

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
     * @return идентификатор модели «Конкуренция против монополии»
     */
    @Override
    public String getModelType() {
        return "CompetitionVsMonopoly";
    }
}
