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
 * Обновлённый солвер для Кривой Филлипса, который формирует три блока данных:
 * 1) "scatter"    — точки (u, π_short) для scatter plot;
 * 2) "timeseries" — временные ряды безработицы и инфляции по «годам»;
 * 3) "loops"      — коротко- и долгосрочная кривая (u, π) для построения «петель».
 */
@Component
public class PhillipsCurveSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Считываем параметры: pi0, u0, alpha, u_n, pi_e
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double pi0   = (Double) ParameterTypeConverter.fromString(paramMap.get("pi0"),   "double");
        double u0    = (Double) ParameterTypeConverter.fromString(paramMap.get("u0"),    "double");
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double");
        double u_n   = (Double) ParameterTypeConverter.fromString(paramMap.get("u_n"),   "double");
        double pi_e  = (Double) ParameterTypeConverter.fromString(paramMap.get("pi_e"),  "double");

        // 2) Строим диапазон безработицы: от (u_n - 4) до (u_n + 4)
        int N = 40;
        double[] uArr = ChartDataUtil.range(u_n - 4, u_n + 4, N);

        // 3) Вычисляем краткосрочную и долгосрочную инфляцию
        double[] piShort = new double[N];
        double[] piLong  = new double[N];
        for (int i = 0; i < N; i++) {
            // π_short = π_e - α·(u - u_n)
            piShort[i] = pi_e - alpha * (uArr[i] - u_n);
            // π_long = π_e (вертикальная линия при u = u_n)
            piLong[i]  = pi_e;
        }

        // === 4) Блок "scatter" ===
        // Клиент ожидает: "points": List<Map<"unemployment",Number>,<"inflation",Number>>
        List<Map<String, Number>> scatterPoints = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            scatterPoints.add(Map.of(
                    "unemployment", uArr[i],
                    "inflation",    piShort[i]
            ));
        }
        Map<String, Object> scatterData = new LinkedHashMap<>();
        scatterData.put("points", scatterPoints);

        // === 5) Блок "timeseries" ===
        // Клиент ожидает два списка:
        //   - "unemployment": List<Map<"year",Number>,<"unemployment",Number>>
        //   - "inflation":    List<Map<"year",Number>,<"inflation",Number>>
        List<Map<String, Number>> tsUnemp  = new ArrayList<>();
        List<Map<String, Number>> tsInfl   = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            tsUnemp.add(Map.of(
                    "year",         i,            // «год» = индекс
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

        // === 6) Блок "loops" ===
        // Клиент ожидает:
        //   - "short_run": List<Map<"unemployment",Number>,<"inflation",Number>>
        //   - "long_run":  List<Map<"unemployment",Number>,<"inflation",Number>>
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

        // === 7) Собираем итоговый JSON-объект с тремя ключами ===
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("scatter",    scatterData);
        allCharts.put("timeseries", timeseriesData);
        allCharts.put("loops",      loopsData);

        // === 8) Упаковываем в DTO и возвращаем результат ===
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
        return "PhillipsCurve";
    }
}
