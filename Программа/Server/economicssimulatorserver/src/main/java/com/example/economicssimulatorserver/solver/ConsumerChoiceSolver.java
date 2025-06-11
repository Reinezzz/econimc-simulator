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


@Component
public class ConsumerChoiceSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double I     = (Double) ParameterTypeConverter.fromString(paramMap.get("I"),     "double");
        double Px    = (Double) ParameterTypeConverter.fromString(paramMap.get("Px"),    "double");
        double Py    = (Double) ParameterTypeConverter.fromString(paramMap.get("Py"),    "double");
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double");
        double U     = (Double) ParameterTypeConverter.fromString(paramMap.get("U"),     "double");

        double Xopt = alpha * I / Px;
        double Yopt = (1 - alpha) * I / Py;

        int N = 40;
        double Xmax = I / Px * 1.2;
        double[] Xs = ChartDataUtil.range(0, Xmax, N);
        double[] Ybl = new double[N];
        for (int i = 0; i < N; i++) {
            Ybl[i] = (I - Px * Xs[i]) / Py;
        }
        List<Map<String, Double>> budgetPoints = ChartDataUtil.pointsList(Xs, Ybl);

        double[] YindiffBase = new double[N];
        for (int i = 0; i < N; i++) {
            if (Xs[i] > 0 && alpha < 1.0) {
                YindiffBase[i] = Math.pow(U / Math.pow(Xs[i], alpha), 1.0 / (1.0 - alpha));
            } else {
                YindiffBase[i] = 0.0;
            }
        }
        List<Map<String, Double>> indifferenceCurve = ChartDataUtil.pointsList(Xs, YindiffBase);

        List<List<Map<String, Double>>> indifferenceFamily = new ArrayList<>();
        double[] levels = { U, U * 0.8, U * 0.6 };
        for (double level : levels) {
            double[] Yind = new double[N];
            for (int i = 0; i < N; i++) {
                if (Xs[i] > 0 && alpha < 1.0) {
                    Yind[i] = Math.pow(level / Math.pow(Xs[i], alpha), 1.0 / (1.0 - alpha));
                } else {
                    Yind[i] = 0.0;
                }
            }
            indifferenceFamily.add(ChartDataUtil.pointsList(Xs, Yind));
        }

        double Py_new = Py * 1.10;

        double Xopt2 = alpha * I / Px;
        double Yopt2 = (1 - alpha) * I / Py_new;

        double eH = Math.pow(Px, alpha) * Math.pow(Py_new, 1.0 - alpha) * U
                / (Math.pow(alpha, alpha) * Math.pow(1.0 - alpha, 1.0 - alpha));
        double Xh = alpha * eH / Px;
        double Yh = (1.0 - alpha) * eH / Py_new;

        Map<String, Object> indiffCurvesData = new LinkedHashMap<>();
        indiffCurvesData.put("indifference_curves", indifferenceFamily);
        indiffCurvesData.put("budget", budgetPoints);

        Map<String, Object> optimumMapData = new LinkedHashMap<>();
        optimumMapData.put("indifference_curve", indifferenceCurve);
        optimumMapData.put("budget", budgetPoints);
        optimumMapData.put("optimum", Map.of(
                "x", Xopt,
                "y", Yopt
        ));

        double[] YindiffNew = new double[N];
        for (int i = 0; i < N; i++) {
            if (Xs[i] > 0 && alpha < 1.0) {
                YindiffNew[i] = Math.pow(U / Math.pow(Xs[i], alpha), 1.0 / (1.0 - alpha)) * (Py / Py_new);
            } else {
                YindiffNew[i] = 0.0;
            }
        }
        List<Map<String, Double>> indiffAfter = ChartDataUtil.pointsList(Xs, YindiffNew);

        List<Map<String, Number>> substitutionSegment = List.of(
                Map.of("x", Xopt, "y", Yopt),
                Map.of("x", Xh,    "y", Yh)
        );
        List<Map<String, Number>> incomeSegment = List.of(
                Map.of("x", Xh,    "y", Yh),
                Map.of("x", Xopt2, "y", Yopt2)
        );

        Map<String, Object> incomeSubstData = new LinkedHashMap<>();
        incomeSubstData.put("before",       indifferenceCurve);
        incomeSubstData.put("after",        indiffAfter);
        incomeSubstData.put("substitution", substitutionSegment);
        incomeSubstData.put("income",       incomeSegment);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("indifference_curves", indiffCurvesData);
        allCharts.put("optimum_map",         optimumMapData);
        allCharts.put("income_substitution", incomeSubstData);

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
        return "ConsumerChoice";
    }
}
