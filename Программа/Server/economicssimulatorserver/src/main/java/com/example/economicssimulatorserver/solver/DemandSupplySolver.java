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
 * Солвер базовой модели спроса и предложения.
 * <p>
 * Считает равновесные цену и объём, излишки и отображает
 * сдвиги кривых.
 */
@Component
public class DemandSupplySolver implements EconomicModelSolver {

    /**
     * Вычисляет точки пересечения спроса и предложения и связанные показатели.
     *
     * @param request параметры функций спроса и предложения
     * @return ответ с графиками, описывающими рынок
     */
    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double a     = (Double) ParameterTypeConverter.fromString(paramMap.get("a"), "double");
        double b     = (Double) ParameterTypeConverter.fromString(paramMap.get("b"), "double");
        double c     = (Double) ParameterTypeConverter.fromString(paramMap.get("c"), "double");
        double d     = (Double) ParameterTypeConverter.fromString(paramMap.get("d"), "double");
        double P_min = (Double) ParameterTypeConverter.fromString(paramMap.get("P_min"), "double");
        double P_max = (Double) ParameterTypeConverter.fromString(paramMap.get("P_max"), "double");

        double[] P = ChartDataUtil.range(P_min, P_max, 40);

        double[] Qd = new double[P.length];
        double[] Qs = new double[P.length];
        for (int i = 0; i < P.length; i++) {
            Qd[i] = a - b * P[i];
            Qs[i] = c + d * P[i];
        }

        double Peq = (a - c) / (b + d);
        double Qeq = c + d * Peq;

        double P_int   = a / b;
        double P_int_s = -c / d;

        Map<String, Object> supplyDemandData = new LinkedHashMap<>();
        supplyDemandData.put("demand",   ChartDataUtil.pointsList(P, Qd));
        supplyDemandData.put("supply",   ChartDataUtil.pointsList(P, Qs));
        supplyDemandData.put("equilibrium", Map.of(
                "quantity", Qeq,
                "price",    Peq
        ));

        Map<String, Object> surplusAreaData = new LinkedHashMap<>();
        surplusAreaData.put("demand",   ChartDataUtil.pointsList(P, Qd));
        surplusAreaData.put("supply",   ChartDataUtil.pointsList(P, Qs));
        surplusAreaData.put("equilibrium", Map.of(
                "quantity", Qeq,
                "price",    Peq
        ));

        List<Map<String, Number>> consumerArea = new ArrayList<>();
        consumerArea.add(Map.of("quantity", 0,   "price", P_int));
        consumerArea.add(Map.of("quantity", 0,   "price", Peq));
        consumerArea.add(Map.of("quantity", Qeq, "price", Peq));
        surplusAreaData.put("consumer_surplus_area", consumerArea);

        List<Map<String, Number>> producerArea = new ArrayList<>();
        producerArea.add(Map.of("quantity", 0,    "price", P_int_s));
        producerArea.add(Map.of("quantity", 0,    "price", Peq));
        producerArea.add(Map.of("quantity", Qeq,  "price", Peq));
        surplusAreaData.put("producer_surplus_area", producerArea);

        List<List<Map<String, Double>>> demandShifts  = new ArrayList<>();
        List<List<Map<String, Double>>> supplyShifts  = new ArrayList<>();
        List<Map<String, Number>>           equilibriums = new ArrayList<>();

        int steps = 5;
        double deltaA = a / steps;
        for (int i = 0; i < steps; i++) {
            double a_i = a + deltaA * i;
            double[] Qd_i = new double[P.length];
            for (int j = 0; j < P.length; j++) {
                Qd_i[j] = a_i - b * P[j];
            }
            double[] Qs_i = Qs;

            List<Map<String, Double>> demandFrame = ChartDataUtil.pointsList(P, Qd_i);
            List<Map<String, Double>> supplyFrame = ChartDataUtil.pointsList(P, Qs_i);

            double Peq_i = (a_i - c) / (b + d);
            double Qeq_i = c + d * Peq_i;

            demandShifts.add(demandFrame);
            supplyShifts.add(supplyFrame);
            equilibriums.add(Map.of("quantity", Qeq_i, "price", Peq_i));
        }

        Map<String, Object> shiftAnimationData = new LinkedHashMap<>();
        shiftAnimationData.put("demand_shifts", demandShifts);
        shiftAnimationData.put("supply_shifts", supplyShifts);
        shiftAnimationData.put("equilibriums",   equilibriums);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("supply_demand",   supplyDemandData);
        allCharts.put("surplus_area",    surplusAreaData);
        allCharts.put("shift_animation", shiftAnimationData);

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
     * @return идентификатор модели спроса и предложения
     */
    @Override
    public String getModelType() {
        return "DemandSupply";
    }
}
