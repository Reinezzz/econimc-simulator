package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.util.ParameterTypeConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Обновлённый солвер для модели роста Солоу, который сразу формирует три блока данных:
 * 1) "trajectories" — траектории капитала и выпуска;
 * 2) "phase"        — фазовая диаграмма (K против ΔK);
 * 3) "statics"      — сравнительная статика (две траектории выпуска при разных s).
 */
@Component
public class SolowGrowthSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Считываем параметры: s, n, delta, alpha, A, K0, L0
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double s     = (Double) ParameterTypeConverter.fromString(paramMap.get("s"),     "double"); // ставка сбережений
        double n     = (Double) ParameterTypeConverter.fromString(paramMap.get("n"),     "double"); // прирост населения
        double delta = (Double) ParameterTypeConverter.fromString(paramMap.get("delta"), "double"); // амортизация
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double"); // доля капитала
        double A     = (Double) ParameterTypeConverter.fromString(paramMap.get("A"),     "double"); // технология
        double K0    = (Double) ParameterTypeConverter.fromString(paramMap.get("K0"),    "double"); // начальный капитал
        double L0    = (Double) ParameterTypeConverter.fromString(paramMap.get("L0"),    "double"); // начальная рабочая сила

        // 2) Задаём горизонт моделирования T=40
        int T = 40;
        double[] timeArr = new double[T];
        double[] K       = new double[T];
        double[] Y       = new double[T];
        double[] L       = new double[T];

        // Начальные условия
        K[0] = K0;
        L[0] = L0;
        timeArr[0] = 0;

        // 3) Симулируем траекторию: для t=1..T-1 растём L, считаем Y_prev, обновляем K
        for (int t = 0; t < T; t++) {
            timeArr[t] = t;
            if (t > 0) {
                // Рост рабочей силы
                L[t] = L[t - 1] * (1 + n);
                // Предыдущее Y для расчёта накопления
                double Yprev = A * Math.pow(K[t - 1], alpha) * Math.pow(L[t - 1], 1 - alpha);
                // Уравнение Солоу: K_t = K_{t-1} + s·Y_{t-1} - δ·K_{t-1}
                K[t] = K[t - 1] + s * Yprev - delta * K[t - 1];
            }
            // Если t == 0, то L[0] уже задано выше
            // Считаем Y(t)
            Y[t] = A * Math.pow(K[t], alpha) * Math.pow(L[t], 1 - alpha);
        }

        // 4) Вычисляем устойчивое состояние (steady state), но для графиков оно не нужно напрямую
        double K_ss = Math.pow((s * A) / (n + delta), 1.0 / (1 - alpha)) * L0;
        double Y_ss = A * Math.pow(K_ss, alpha) * Math.pow(L0, 1 - alpha);

        // === 5) Формируем блок "trajectories" ===
        // Клиент ожидает:
        //   - "capital": List<Map<"time",Number>,<"capital",Number>>
        //   - "output":  List<Map<"time",Number>,<"output",Number>>
        List<Map<String, Number>> capitalSeries = new ArrayList<>();
        List<Map<String, Number>> outputSeries  = new ArrayList<>();
        for (int t = 0; t < T; t++) {
            // Явно создаём Map с ключами "time" и "capital"/"output"
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

        // === 6) Формируем блок "phase" ===
        // Клиент ожидает:
        //   - "phase": List<Map<"capital",Number>,<"capital_change",Number>>
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

        // === 7) Формируем блок "statics" ===
        // Сравнительная статика: "baseline" и "high_savings" (s увеличено на 10%)
        // Клиент ожидает:
        //   - "scenarios": Map< String, List<Map<"time",Number>,<"output",Number>> >
        double sHigh = s * 1.10; // повышенная ставка сбережений
        Map<String, List<Map<String, Number>>> scenarios = new LinkedHashMap<>();

        // Функция для генерации Y-траектории при заданном s_param
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
                // Собираем точку ("time", "output")
                outSeries.add(Map.of(
                        "time",   t,
                        "output", Yloc[t]
                ));
            }
            return outSeries;
        };

        // "baseline"
        scenarios.put("baseline", simulateOutput.apply(s));
        // "high_savings"
        scenarios.put("high_savings", simulateOutput.apply(sHigh));

        Map<String, Object> staticsData = new LinkedHashMap<>();
        staticsData.put("scenarios", scenarios);

        // === 8) Собираем единый JSON-объект с тремя ключами ===
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("trajectories", trajectoriesData);
        allCharts.put("phase",        phaseData);
        allCharts.put("statics",      staticsData);

        // === 9) Упаковываем всё в ModelResultDto и возвращаем ===
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
