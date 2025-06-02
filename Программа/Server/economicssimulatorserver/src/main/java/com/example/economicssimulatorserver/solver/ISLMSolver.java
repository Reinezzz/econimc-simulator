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
 * Обновлённый солвер для модели IS-LM, который формирует сразу три блока данных:
 * 1) "is_lm"     — точки IS, точки LM и точка равновесия { "income", "rate" };
 * 2) "surface"   — семейство кривых IS для разных значений G (каждая кривая — List<Map<"income",Number>,<"rate",Number>>);
 * 3) "timeseries"— временные ряды равновесных Y и r при постепенном изменении G.
 */
@Component
public class ISLMSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // --- 1) Считываем входные параметры ---
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        // Параметры IS: C0, c1, I0, b, G, T
        double C0 = (Double) ParameterTypeConverter.fromString(paramMap.get("C0"), "double");
        double c1 = (Double) ParameterTypeConverter.fromString(paramMap.get("c1"), "double");
        double I0 = (Double) ParameterTypeConverter.fromString(paramMap.get("I0"), "double");
        double b  = (Double) ParameterTypeConverter.fromString(paramMap.get("b"),  "double");
        double G0 = (Double) ParameterTypeConverter.fromString(paramMap.get("G"),  "double");
        double T  = (Double) ParameterTypeConverter.fromString(paramMap.get("T"),  "double");

        // Параметры LM: Ms, L0, l1, l2
        double Ms = (Double) ParameterTypeConverter.fromString(paramMap.get("Ms"), "double");
        double L0 = (Double) ParameterTypeConverter.fromString(paramMap.get("L0"), "double");
        double l1 = (Double) ParameterTypeConverter.fromString(paramMap.get("l1"), "double");
        double l2 = (Double) ParameterTypeConverter.fromString(paramMap.get("l2"), "double");

        // --- 2) Строим базовые кривые IS и LM и находим равновесие ---
        int N = 40;
        // Процентные ставки от 0 до 0.20 (0%–20%)
        double[] rArr = ChartDataUtil.range(0, 0.20, N);

        // Вычисляем Y_IS и Y_LM при исходном G0
        double[] Y_IS = new double[N];
        double[] Y_LM = new double[N];
        for (int i = 0; i < N; i++) {
            double r = rArr[i];
            Y_IS[i] = (1.0 / (1 - c1)) * (C0 + I0 - b * r + G0 - c1 * T);
            Y_LM[i] = (Ms - L0 + l2 * r) / l1;
        }

        // Ищем приблизительное равновесие (минимальная разница Y_IS - Y_LM)
        double minDist = Double.MAX_VALUE;
        double eq_r = 0.0, eq_Y = 0.0;
        for (int i = 0; i < N; i++) {
            double dist = Math.abs(Y_IS[i] - Y_LM[i]);
            if (dist < minDist) {
                minDist = dist;
                eq_r = rArr[i];
                eq_Y = (Y_IS[i] + Y_LM[i]) / 2.0;
            }
        }

        // --- 3) Формируем блок "is_lm" ---
        // Клиент ожидает:
        //   "IS":          List<Map<"income", Number>, <"rate", Number>>
        //   "LM":          List<Map<"income", Number>, <"rate", Number>>
        //   "equilibrium": Map<"income", Number>, <"rate", Number>
        List<Map<String, Number>> isPoints = new ArrayList<>();
        List<Map<String, Number>> lmPoints = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            double incomeIS = Y_IS[i];
            double incomeLM = Y_LM[i];
            double rate = rArr[i];
            isPoints.add(Map.of("income", incomeIS, "rate", rate));
            lmPoints.add(Map.of("income", incomeLM, "rate", rate));
        }
        Map<String, Object> islmData = new LinkedHashMap<>();
        islmData.put("IS", isPoints);
        islmData.put("LM", lmPoints);
        islmData.put("equilibrium", Map.of("income", eq_Y, "rate", eq_r));

        // --- 4) Формируем блок "surface": семейство кривых IS при разных G ---
        // Клиент ожидает:
        //   "surface": List< List<Map<"income", Number>, <"rate", Number>> >
        // Мы задаём 5 срезов G: от 0.5·G0 до 1.5·G0
        int surfaceSlices = 5;
        double Gmin = G0 * 0.5;
        double Gmax = G0 * 1.5;
        double dG = (Gmax - Gmin) / (surfaceSlices - 1);

        List<List<Map<String, Number>>> surface = new ArrayList<>();
        for (int s = 0; s < surfaceSlices; s++) {
            double Gs = Gmin + dG * s;
            List<Map<String, Number>> slicePoints = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                double r = rArr[i];
                double Yi = (1.0 / (1 - c1)) * (C0 + I0 - b * r + Gs - c1 * T);
                slicePoints.add(Map.of("income", Yi, "rate", r));
            }
            surface.add(slicePoints);
        }
        Map<String, Object> surfaceData = new LinkedHashMap<>();
        surfaceData.put("slices", surface);

        // --- 5) Формируем блок "timeseries": динамика равновесного Y и r при изменении G во времени ---
        // Клиент ожидает:
        //   "policy": List<Map<"time", Number>, <"income", Number>>
        //   "rate":   List<Map<"time", Number>, <"rate", Number>>
        // Пусть за 40 периодов (t=0..39) G линейно растёт от G0 до G0*1.5.
        int Tsteps = 40;
        List<Map<String, Number>> policySeries = new ArrayList<>();
        List<Map<String, Number>> rateSeries   = new ArrayList<>();
        for (int t = 0; t < Tsteps; t++) {
            double Gt = G0 + (G0 * 0.5) * t / (Tsteps - 1); // от G0 до 1.5·G0
            // Снова ищем равновесие IS-LM при новом Gt
            double[] Y_IS_t = new double[N];
            double[] Y_LM_t = new double[N];
            for (int i = 0; i < N; i++) {
                double r = rArr[i];
                Y_IS_t[i] = (1.0 / (1 - c1)) * (C0 + I0 - b * r + Gt - c1 * T);
                Y_LM_t[i] = (Ms - L0 + l2 * r) / l1;
            }
            // Находим ближайший индекс i*, где |Y_IS_t[i] - Y_LM_t[i]| минимально
            double minD = Double.MAX_VALUE;
            double eq_r_t = 0.0, eq_Y_t = 0.0;
            for (int i = 0; i < N; i++) {
                double dval = Math.abs(Y_IS_t[i] - Y_LM_t[i]);
                if (dval < minD) {
                    minD = dval;
                    eq_r_t = rArr[i];
                    eq_Y_t = (Y_IS_t[i] + Y_LM_t[i]) / 2.0;
                }
            }
            policySeries.add(Map.of("time", (Number) t, "income", eq_Y_t));
            rateSeries.add(Map.of("time", (Number) t, "rate",   eq_r_t));
        }

        Map<String, Object> tsData = new LinkedHashMap<>();
        tsData.put("policy", policySeries);
        tsData.put("rate",   rateSeries);

        // --- 6) Собираем единый JSON-объект с тремя верхнеуровневыми ключами ---
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("is_lm",     islmData);
        allCharts.put("surface",   surfaceData);
        allCharts.put("timeseries", tsData);

        // --- 7) Сериализуем и возвращаем DTO ---
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
        return "ISLM";
    }
}
