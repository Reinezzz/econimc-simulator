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
 * Солвер кривой Филлипса, показывающей связь между инфляцией
 * и безработицей.
 */
@Component
public class PhillipsCurveSolver implements EconomicModelSolver {

    /**
     * Рассчитывает краткосрочную и долгосрочную кривые Филлипса и соответствующие временные ряды.
     *
     * @param request параметры: ожидаемая инфляция, естественный уровень безработицы и др.
     * @return ответ с графиками для кривой Филлипса
     */
    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double pi0   = (Double) ParameterTypeConverter.fromString(paramMap.get("pi0"),   "double");
        double u0    = (Double) ParameterTypeConverter.fromString(paramMap.get("u0"),    "double");
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double");
        double u_n   = (Double) ParameterTypeConverter.fromString(paramMap.get("u_n"),   "double");
        double pi_e  = (Double) ParameterTypeConverter.fromString(paramMap.get("pi_e"),  "double");

        int N = 40;
        double[] uArr = ChartDataUtil.range(u_n - 4, u_n + 4, N);

        double[] piShort = new double[N];
        double[] piLong  = new double[N];
        for (int i = 0; i < N; i++) {
            piShort[i] = pi_e - alpha * (uArr[i] - u_n);
            piLong[i]  = pi_e;
        }

        List<Map<String, Number>> scatterPoints = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            scatterPoints.add(Map.of(
                    "unemployment", uArr[i],
                    "inflation",    piShort[i]
            ));
        }
        Map<String, Object> scatterData = new LinkedHashMap<>();
        scatterData.put("points", scatterPoints);

        List<Map<String, Number>> tsUnemp  = new ArrayList<>();
        List<Map<String, Number>> tsInfl   = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            tsUnemp.add(Map.of(
                    "year",         i,
                    "unemployment", uArr[i]
            ));
            tsInfl.add(Map.of(
                    "year",      i,
                    "inflation", piShort[i]
            ));
        }
        Map<String, Object> timeseriesData = new LinkedHashMap<>();
        timeseriesData.put("unemployment", tsUnemp);
        timeseriesData.put("inflation",    tsInfl);

        List<Map<String, Number>> shortRun = new ArrayList<>();
        List<Map<String, Number>> longRun  = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            shortRun.add(Map.of(
                    "unemployment", uArr[i],
                    "inflation",    piShort[i]
            ));
            longRun.add(Map.of(
                    "unemployment", uArr[i],
                    "inflation",    piLong[i]
            ));
        }
        Map<String, Object> loopsData = new LinkedHashMap<>();
        loopsData.put("short_run", shortRun);
        loopsData.put("long_run",  longRun);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("scatter",    scatterData);
        allCharts.put("timeseries", timeseriesData);
        allCharts.put("loops",      loopsData);

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
     * @return идентификатор модели кривой Филлипса
     */
    @Override
    public String getModelType() {
        return "PhillipsCurve";
    }
}
