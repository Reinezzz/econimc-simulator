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
 * Солвер для макроэкономической модели IS–LM.
 * <p>
 * Строит графики равновесия, поверхности и временные ряды,
 * демонстрирующие влияние фискальной политики.
 */
@Component
public class ISLMSolver implements EconomicModelSolver {

    /**
     * Вычисляет пересечение кривых IS и LM и сопутствующие сценарии.
     *
     * @param request параметры товарного и денежного рынков
     * @return ответ с данными для графиков IS–LM
     */
    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double C0 = (Double) ParameterTypeConverter.fromString(paramMap.get("C0"), "double");
        double c1 = (Double) ParameterTypeConverter.fromString(paramMap.get("c1"), "double");
        double I0 = (Double) ParameterTypeConverter.fromString(paramMap.get("I0"), "double");
        double b  = (Double) ParameterTypeConverter.fromString(paramMap.get("b"),  "double");
        double G0 = (Double) ParameterTypeConverter.fromString(paramMap.get("G"),  "double");
        double T  = (Double) ParameterTypeConverter.fromString(paramMap.get("T"),  "double");

        double Ms = (Double) ParameterTypeConverter.fromString(paramMap.get("Ms"), "double");
        double L0 = (Double) ParameterTypeConverter.fromString(paramMap.get("L0"), "double");
        double l1 = (Double) ParameterTypeConverter.fromString(paramMap.get("l1"), "double");
        double l2 = (Double) ParameterTypeConverter.fromString(paramMap.get("l2"), "double");

        int N = 40;

        double[] rArr = ChartDataUtil.range(0, 0.20, N);

        double[] Y_IS = new double[N];
        double[] Y_LM = new double[N];
        for (int i = 0; i < N; i++) {
            double r = rArr[i];
            Y_IS[i] = (1.0 / (1 - c1)) * (C0 + I0 - b * r + G0 - c1 * T);
            Y_LM[i] = (Ms - L0 + l2 * r) / l1;
        }

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

        int Tsteps = 40;
        List<Map<String, Number>> policySeries = new ArrayList<>();
        List<Map<String, Number>> rateSeries   = new ArrayList<>();
        for (int t = 0; t < Tsteps; t++) {
            double Gt = G0 + (G0 * 0.5) * t / (Tsteps - 1);
            double[] Y_IS_t = new double[N];
            double[] Y_LM_t = new double[N];
            for (int i = 0; i < N; i++) {
                double r = rArr[i];
                Y_IS_t[i] = (1.0 / (1 - c1)) * (C0 + I0 - b * r + Gt - c1 * T);
                Y_LM_t[i] = (Ms - L0 + l2 * r) / l1;
            }
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

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("is_lm",     islmData);
        allCharts.put("surface",   surfaceData);
        allCharts.put("timeseries", tsData);

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
     * @return идентификатор модели IS–LM
     */
    @Override
    public String getModelType() {
        return "ISLM";
    }
}
