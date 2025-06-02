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
 * Обновлённый солвер для сравнения совершенной конкуренции и монополии.
 * Возвращает сразу три блока данных:
 * 1) "profit_curves"   — две серии точек { "quantity", "profit" } для конкуренции и монополии;
 * 2) "comparison_hist" — категории ["Цена","Количество","Прибыль"] и два списка значений для конкуренции и монополии;
 * 3) "deadweight_area" — кривые спроса/предложения + численные Q_eq для конкуренции и монополии.
 */
@Component
public class CompetitionSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Собираем входные параметры в Map<String,String>
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        // Параметры спроса/предложения: Qd = a - bP, Qs = c + dP
        double a          = (Double) ParameterTypeConverter.fromString(paramMap.get("a"),  "double");
        double b          = (Double) ParameterTypeConverter.fromString(paramMap.get("b"),  "double");
        double c          = (Double) ParameterTypeConverter.fromString(paramMap.get("c"),  "double");
        double d          = (Double) ParameterTypeConverter.fromString(paramMap.get("d"),  "double");
        // Предельные и постоянные издержки
        double MC         = (Double) ParameterTypeConverter.fromString(paramMap.get("MC"), "double");
        double FC         = (Double) ParameterTypeConverter.fromString(paramMap.get("FC"), "double");
        // Тип рынка (не влияет на расчёты, но может пригодиться позже)
        String marketType = paramMap.getOrDefault("market_type", "competition");

        // 2) Строим набор цен P и соответствующие Qd, Qs
        //    В качестве верхней границы цены возьмём чуть больше точки пересечения спроса с осью (где Qd=0 => P=a/b)
        double P_max = a / b * 1.2;
        double[] P   = ChartDataUtil.range(0, P_max, 40);

        double[] Qd = new double[P.length];
        double[] Qs = new double[P.length];
        for (int i = 0; i < P.length; i++) {
            Qd[i] = a - b * P[i];
            Qs[i] = c + d * P[i];
        }

        // 3) Находим равновесие в условиях конкуренции (Qd = Qs)
        double eqPriceCompetition    = (a - c) / (b + d);
        double eqQuantityCompetition = c + d * eqPriceCompetition;

        // 4) Находим равновесие монополии:
        //    MR = MC, где при обратном спросе P = (a - Q)/b,
        //    TR = P·Q = (aQ - Q^2/b), MR = dTR/dQ = a - 2Q/b => solve a - 2Q/b = MC => Q = (a - MC)*b/2
        double eqQuantityMonopoly = (a - MC) * b / 2.0;
        double eqPriceMonopoly    = (a - eqQuantityMonopoly) / b;

        // 5) Считаем прибыль в равновесиях:
        //    profit = (P_eq - MC) * Q_eq - FC
        double profitCompetition = (eqPriceCompetition - MC) * eqQuantityCompetition - FC;
        double profitMonopoly    = (eqPriceMonopoly    - MC) * eqQuantityMonopoly    - FC;

        // === БЛОК 1: profit_curves ===
        // Клиент ожидает: chartData.get("profit_curves") → Map с ключами "competition" и "monopoly",
        // где каждое — List<Map<"quantity",Number>,<"profit",Number>>.
        List<Map<String, Number>> competitionCurve = new ArrayList<>();
        List<Map<String, Number>> monopolyCurve    = new ArrayList<>();

        for (int i = 0; i < P.length; i++) {
            double price = P[i];
            double qSup  = Qs[i];
            double qDem  = Qd[i];

            // Профит в конкуренции при данной цене = (price - MC) * qSup - FC
            double profitC = (price - MC) * qSup - FC;
            competitionCurve.add(Map.of(
                    "quantity", qSup,
                    "profit",   profitC
            ));

            // Профит монополии при данной цене = (price - MC) * qDem - FC,
            // поскольку монополист берёт ценовую функцию спроса
            double profitM = (price - MC) * qDem - FC;
            monopolyCurve.add(Map.of(
                    "quantity", qDem,
                    "profit",   profitM
            ));
        }

        Map<String, Object> profitData = new LinkedHashMap<>();
        profitData.put("competition", competitionCurve);
        profitData.put("monopoly",    monopolyCurve);

        // === БЛОК 2: comparison_hist ===
        // Клиент ожидает:
        //   "categories": List<String> — например ["Цена","Количество","Прибыль"]
        //   "competition": List<Number> (три значения: цена, количество, прибыль)
        //   "monopoly":    List<Number>
        List<String> categories = List.of("Цена", "Количество", "Прибыль");
        List<Number> compValues  = List.of(eqPriceCompetition, eqQuantityCompetition, profitCompetition);
        List<Number> monoValues  = List.of(eqPriceMonopoly, eqQuantityMonopoly, profitMonopoly);

        Map<String, Object> histData = new LinkedHashMap<>();
        histData.put("categories",  categories);
        histData.put("competition",  compValues);
        histData.put("monopoly",     monoValues);

        // === БЛОК 3: deadweight_area ===
        // Клиент ожидает:
        //   "demand": List<Map<"quantity",Number>,<"price",Number>>
        //   "supply": List<Map<"quantity",Number>,<"price",Number>>
        //   "monopolyQ": Number
        //   "competitionQ": Number
        Map<String, Object> dwlData = new LinkedHashMap<>();
        dwlData.put("demand",    ChartDataUtil.pointsList(P, Qd));
        dwlData.put("supply",    ChartDataUtil.pointsList(P, Qs));
        dwlData.put("monopolyQ", eqQuantityMonopoly);
        dwlData.put("competitionQ", eqQuantityCompetition);

        // === Собираем «объединённый» JSON-объект с тремя ключами ===
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("profit_curves",   profitData);
        allCharts.put("comparison_hist", histData);
        allCharts.put("deadweight_area", dwlData);

        // === Упаковываем в DTO и возвращаем ===
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
        return "CompetitionVsMonopoly";
    }
}
