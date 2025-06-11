package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.util.ParameterTypeConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SolowGrowthSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double s     = (Double) ParameterTypeConverter.fromString(paramMap.get("s"),     "double"); // ставка сбережений
        double n     = (Double) ParameterTypeConverter.fromString(paramMap.get("n"),     "double"); // прирост населения
        double delta = (Double) ParameterTypeConverter.fromString(paramMap.get("delta"), "double"); // амортизация
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double"); // доля капитала
        double A     = (Double) ParameterTypeConverter.fromString(paramMap.get("A"),     "double"); // технология
        double K0    = (Double) ParameterTypeConverter.fromString(paramMap.get("K0"),    "double"); // начальный капитал
        double L0    = (Double) ParameterTypeConverter.fromString(paramMap.get("L0"),    "double"); // начальная рабочая сила

        int T = 40;
        double[] timeArr = new double[T];
        double[] K       = new double[T];
        double[] Y       = new double[T];
        double[] L       = new double[T];

        K[0] = K0;
        L[0] = L0;
        timeArr[0] = 0;

        for (int t = 0; t < T; t++) {
            timeArr[t] = t;
            if (t > 0) {
                L[t] = L[t - 1] * (1 + n);
                double Yprev = A * Math.pow(K[t - 1], alpha) * Math.pow(L[t - 1], 1 - alpha);
                K[t] = K[t - 1] + s * Yprev - delta * K[t - 1];
            }

            Y[t] = A * Math.pow(K[t], alpha) * Math.pow(L[t], 1 - alpha);
        }

        double K_ss = Math.pow((s * A) / (n + delta), 1.0 / (1 - alpha)) * L0;
        double Y_ss = A * Math.pow(K_ss, alpha) * Math.pow(L0, 1 - alpha);

        List<Map<String, Number>> capitalSeries = new ArrayList<>();
        List<Map<String, Number>> outputSeries  = new ArrayList<>();
        for (int t = 0; t < T; t++) {
            capitalSeries.add(Map.of(
                    "time",    timeArr[t],
                    "capital", K[t]
            ));
            outputSeries.add(Map.of(
                    "time",  timeArr[t],
                    "output", Y[t]
            ));
        }
        Map<String, Object> trajectoriesData = new LinkedHashMap<>();
        trajectoriesData.put("capital", capitalSeries);
        trajectoriesData.put("output",  outputSeries);

        List<Map<String, Number>> phasePoints = new ArrayList<>();
        for (int t = 1; t < T; t++) {
            double deltaK = K[t] - K[t - 1];
            phasePoints.add(Map.of(
                    "capital",        K[t],
                    "capital_change", deltaK
            ));
        }
        Map<String, Object> phaseData = new LinkedHashMap<>();
        phaseData.put("phase", phasePoints);

        double sHigh = s * 1.10;
        Map<String, List<Map<String, Number>>> scenarios = new LinkedHashMap<>();

        java.util.function.DoubleFunction<List<Map<String, Number>>> simulateOutput = s_param -> {
            double[] Kloc = new double[T];
            double[] Lloc = new double[T];
            double[] Yloc = new double[T];

            Kloc[0] = K0;
            Lloc[0] = L0;
            List<Map<String, Number>> outSeries = new ArrayList<>();
            for (int t = 0; t < T; t++) {
                if (t > 0) {
                    Lloc[t] = Lloc[t - 1] * (1 + n);
                    double YprevLoc = A * Math.pow(Kloc[t - 1], alpha) * Math.pow(Lloc[t - 1], 1 - alpha);
                    Kloc[t] = Kloc[t - 1] + s_param * YprevLoc - delta * Kloc[t - 1];
                }
                Yloc[t] = A * Math.pow(Kloc[t], alpha) * Math.pow(Lloc[t], 1 - alpha);
                outSeries.add(Map.of(
                        "time",   t,
                        "output", Yloc[t]
                ));
            }
            return outSeries;
        };

        scenarios.put("baseline", simulateOutput.apply(s));
        scenarios.put("high_savings", simulateOutput.apply(sHigh));

        Map<String, Object> staticsData = new LinkedHashMap<>();
        staticsData.put("scenarios", scenarios);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("trajectories", trajectoriesData);
        allCharts.put("phase",        phaseData);
        allCharts.put("statics",      staticsData);

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
        return "SolowGrowth";
    }
}
