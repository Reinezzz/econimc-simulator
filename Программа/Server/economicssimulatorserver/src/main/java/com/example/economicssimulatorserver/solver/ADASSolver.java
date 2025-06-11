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
public class ADASSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double Y_pot    = (Double) ParameterTypeConverter.fromString(paramMap.get("Y_pot"),    "double");
        double P0       = (Double) ParameterTypeConverter.fromString(paramMap.get("P0"),       "double");
        double AD_slope = (Double) ParameterTypeConverter.fromString(paramMap.get("AD_slope"), "double");
        double AS_slope = (Double) ParameterTypeConverter.fromString(paramMap.get("AS_slope"), "double");
        double shock    = (Double) ParameterTypeConverter.fromString(paramMap.get("shock"),    "double");

        int N = 50;
        double[] P = ChartDataUtil.range(P0 - 50, P0 + 50, N);

        double[] Y_AD_base  = new double[N];
        double[] Y_AD_shift = new double[N];
        double[] Y_SRAS     = new double[N];
        for (int i = 0; i < N; i++) {
            double dP = P[i] - P0;
            Y_AD_base[i]  = Y_pot + AD_slope * dP;
            Y_AD_shift[i] = Y_pot + AD_slope * dP + shock;
            Y_SRAS[i]     = Y_pot + AS_slope * dP;
        }

        double minDist = Double.MAX_VALUE;
        double eq_P_post = P0;
        double eq_Y_post = Y_pot;
        for (int i = 0; i < N; i++) {
            double dist = Math.abs(Y_AD_shift[i] - Y_SRAS[i]);
            if (dist < minDist) {
                minDist = dist;
                eq_P_post = P[i];
                eq_Y_post = (Y_AD_shift[i] + Y_SRAS[i]) / 2.0;
            }
        }

        List<Map<String, Number>> seriesAD      = new ArrayList<>();
        List<Map<String, Number>> seriesSRAS    = new ArrayList<>();
        List<Map<String, Number>> seriesLRAS    = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            seriesAD.add(Map.of("x", Y_AD_base[i], "y", P[i]));
            seriesSRAS.add(Map.of("x", Y_SRAS[i], "y", P[i]));
            seriesLRAS.add(Map.of("x", Y_pot, "y", P[i]));
        }
        Map<String, Object> equilibriumData = new LinkedHashMap<>();
        equilibriumData.put("AD",          seriesAD);
        equilibriumData.put("SRAS",        seriesSRAS);
        equilibriumData.put("LRAS",        seriesLRAS);
        equilibriumData.put("equilibrium", Map.of("x", eq_Y_post, "y", eq_P_post));

        List<Map<String, Number>> seriesAD_base   = new ArrayList<>();
        List<Map<String, Number>> seriesAD_after  = new ArrayList<>();
        List<Map<String, Number>> seriesSRAS_base = new ArrayList<>();
        List<Map<String, Number>> seriesSRAS_after= new ArrayList<>();
        List<Map<String, Number>> seriesLRAS2     = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            seriesAD_base.add(Map.of("x", Y_AD_base[i],   "y", P[i]));
            seriesAD_after.add(Map.of("x", Y_AD_shift[i], "y", P[i]));

            seriesSRAS_base.add(Map.of("x", Y_SRAS[i], "y", P[i]));
            seriesSRAS_after.add(Map.of("x", Y_SRAS[i], "y", P[i]));

            seriesLRAS2.add(Map.of("x", Y_pot, "y", P[i]));
        }
        Map<String, Object> shiftsData = new LinkedHashMap<>();
        shiftsData.put("AD",    seriesAD_base);
        shiftsData.put("AD2",   seriesAD_after);
        shiftsData.put("SRAS",  seriesSRAS_base);
        shiftsData.put("SRAS2", seriesSRAS_after);
        shiftsData.put("LRAS",  seriesLRAS2);

        List<Map<String, Number>> seriesAD_post = new ArrayList<>();
        List<Map<String, Number>> seriesSRAS_post= new ArrayList<>();
        List<Map<String, Number>> seriesLRAS3   = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            seriesAD_post.add(Map.of("x", Y_AD_base[i], "y", P[i]));
            seriesSRAS_post.add(Map.of("x", Y_SRAS[i], "y", P[i]));
            seriesLRAS3.add(Map.of("x", Y_pot, "y", P[i]));
        }

        Map<String, Object> gapsData = new LinkedHashMap<>();
        gapsData.put("AD",         seriesAD_post);
        gapsData.put("SRAS",       seriesSRAS_post);
        gapsData.put("LRAS",       seriesLRAS3);
        gapsData.put("potentialY", Y_pot);
        gapsData.put("actualY",    eq_Y_post);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("equilibrium", equilibriumData);
        allCharts.put("shifts",      shiftsData);
        allCharts.put("gaps",        gapsData);

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
        return "ADAS";
    }
}
