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
 * Обновлённый солвер для модели «Спрос и Предложение», который сразу формирует три набора данных:
 * 1. "supply_demand"   — кривые спроса/предложения + точка равновесия;
 * 2. "surplus_area"    — то же + полигоны для излишков потребителя и производителя;
 * 3. "shift_animation" — последовательность сдвинутых кривых и точек равновесия для анимации.
 */
@Component
public class DemandSupplySolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // Собираем входные параметры в Map<имя, строковое значение>
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        // Базовые параметры линейных функций:
        // Qd = a - bP
        // Qs = c + dP
        double a     = (Double) ParameterTypeConverter.fromString(paramMap.get("a"), "double");
        double b     = (Double) ParameterTypeConverter.fromString(paramMap.get("b"), "double");
        double c     = (Double) ParameterTypeConverter.fromString(paramMap.get("c"), "double");
        double d     = (Double) ParameterTypeConverter.fromString(paramMap.get("d"), "double");
        double P_min = (Double) ParameterTypeConverter.fromString(paramMap.get("P_min"), "double");
        double P_max = (Double) ParameterTypeConverter.fromString(paramMap.get("P_max"), "double");

        // 1) Строим равномерную сетку цен (массив P) из ChartDataUtil
        double[] P = ChartDataUtil.range(P_min, P_max, 40);

        // Для каждого P вычисляем Qd и Qs:
        double[] Qd = new double[P.length];
        double[] Qs = new double[P.length];
        for (int i = 0; i < P.length; i++) {
            Qd[i] = a - b * P[i];
            Qs[i] = c + d * P[i];
        }

        // 2) Находим точку равновесия (Qd = Qs):
        //     a - bP = c + dP  =>  P* = (a - c)/(b + d),   Q* = c + d*P*
        double Peq = (a - c) / (b + d);
        double Qeq = c + d * Peq;

        // 3) Находим пересечения с осью цены (для вычисления треугольников излишков):
        //    спрос пересекает ось P при Qd=0 => 0 = a - bP => P_int = a/b
        //    предложение пересекает ось P при Qs=0 => 0 = c + dP => P_int_s = -c/d
        double P_int   = a / b;   // точка пересечения Qd с осью цен
        double P_int_s = -c / d;  // точка пересечения Qs с осью цен

        // === 1. Формируем «supply_demand» (простейший график: только линии и точка равновесия) ===
        Map<String, Object> supplyDemandData = new LinkedHashMap<>();
        supplyDemandData.put("demand",   ChartDataUtil.pointsList(P, Qd));
        supplyDemandData.put("supply",   ChartDataUtil.pointsList(P, Qs));
        supplyDemandData.put("equilibrium", Map.of(
                "quantity", Qeq,
                "price",    Peq
        ));

        // === 2. Формируем «surplus_area» (те же линии + полигоны излишка) ===
        Map<String, Object> surplusAreaData = new LinkedHashMap<>();
        surplusAreaData.put("demand",   ChartDataUtil.pointsList(P, Qd));
        surplusAreaData.put("supply",   ChartDataUtil.pointsList(P, Qs));
        surplusAreaData.put("equilibrium", Map.of(
                "quantity", Qeq,
                "price",    Peq
        ));
        // 2.1. Потребительский излишек: треугольник (0, P_int)→(0, Peq)→(Qeq, Peq)
        List<Map<String, Number>> consumerArea = new ArrayList<>();
        consumerArea.add(Map.of("quantity", 0,   "price", P_int));
        consumerArea.add(Map.of("quantity", 0,   "price", Peq));
        consumerArea.add(Map.of("quantity", Qeq, "price", Peq));
        surplusAreaData.put("consumer_surplus_area", consumerArea);

        // 2.2. Производственный излишек: треугольник (0, P_int_s)→(0, Peq)→(Qeq, Peq)
        List<Map<String, Number>> producerArea = new ArrayList<>();
        producerArea.add(Map.of("quantity", 0,    "price", P_int_s));
        producerArea.add(Map.of("quantity", 0,    "price", Peq));
        producerArea.add(Map.of("quantity", Qeq,  "price", Peq));
        surplusAreaData.put("producer_surplus_area", producerArea);

        // === 3. Формируем «shift_animation» (несколько «кадров» со смещёнными кривыми) ===
        // Идея: пусть мы сдвигаем спрос вправо (увеличиваем a) в несколько шагов, а предложение оставляем без изменений.
        // Визуализатор ожидает:
        //   "demand_shifts": List< List<Map<"quantity",Number>, Map<"price",Number>> >
        //   "supply_shifts": List< List<Map<"quantity",Number>, Map<"price",Number>> >
        //   "equilibriums": List< Map<"quantity",Number> + "price" >
        //
        // Сделаем, например, 5 кадров: каждый следующий кадр — это a_i = a + i*(a/5).
        List<List<Map<String, Double>>> demandShifts  = new ArrayList<>();
        List<List<Map<String, Double>>> supplyShifts  = new ArrayList<>();
        List<Map<String, Number>>           equilibriums = new ArrayList<>();

        int steps = 5;
        double deltaA = a / steps;
        for (int i = 0; i < steps; i++) {
            double a_i = a + deltaA * i;
            // Пересчитаем Qd_i = a_i - bP для массива P
            double[] Qd_i = new double[P.length];
            for (int j = 0; j < P.length; j++) {
                Qd_i[j] = a_i - b * P[j];
            }
            // Qs будет оставаться прежним (Qs_j = c + d*P[j]), но вынесем всё в список точек
            double[] Qs_i = Qs; // просто ссылка на прежний массив

            // Собираем точки для i-го кадра
            List<Map<String, Double>> demandFrame = ChartDataUtil.pointsList(P, Qd_i);
            List<Map<String, Double>> supplyFrame = ChartDataUtil.pointsList(P, Qs_i);

            // Точка равновесия для i-го кадра: P* = (a_i - c)/(b + d), Q* = c + d*P*
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

        // === 4. Собираем «объединённый» результат — три вложенных карты ===
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("supply_demand",   supplyDemandData);
        allCharts.put("surplus_area",    surplusAreaData);
        allCharts.put("shift_animation", shiftAnimationData);

        // Превращаем всё это в JSON-строку и кладём в ModelResultDto
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
        return "DemandSupply";
    }
}
