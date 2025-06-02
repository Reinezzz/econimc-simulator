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
 * Обновлённый солвер для модели AD-AS, формирующий три блока данных:
 * 1) "equilibrium" — построение точек AD, SRAS, LRAS и точки равновесия;
 * 2) "shifts"      — сдвиг AD (shock ≠ 0) «до/после» и неизменные SRAS, LRAS;
 * 3) "gaps"        — подсветка разрыва между потенциальным и фактическим выпуском.
 */
@Component
public class ADASSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Считываем входные параметры
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double Y_pot    = (Double) ParameterTypeConverter.fromString(paramMap.get("Y_pot"),    "double");
        double P0       = (Double) ParameterTypeConverter.fromString(paramMap.get("P0"),       "double");
        double AD_slope = (Double) ParameterTypeConverter.fromString(paramMap.get("AD_slope"), "double");
        double AS_slope = (Double) ParameterTypeConverter.fromString(paramMap.get("AS_slope"), "double");
        double shock    = (Double) ParameterTypeConverter.fromString(paramMap.get("shock"),    "double");

        // 2) Задаём диапазон цен P от (P0-50) до (P0+50), 50 точек
        int N = 50;
        double[] P = ChartDataUtil.range(P0 - 50, P0 + 50, N);

        // 3) Вычисляем соответствующие Y для AD (до шока и после шока) и AS:
        //    AD_base:   Y = Y_pot + AD_slope*(P - P0)       (shock = 0)
        //    AD_shift:  Y = Y_pot + AD_slope*(P - P0) + shock
        //    SRAS:      Y = Y_pot + AS_slope*(P - P0)
        double[] Y_AD_base  = new double[N];
        double[] Y_AD_shift = new double[N];
        double[] Y_SRAS     = new double[N];
        for (int i = 0; i < N; i++) {
            double dP = P[i] - P0;
            Y_AD_base[i]  = Y_pot + AD_slope * dP;
            Y_AD_shift[i] = Y_pot + AD_slope * dP + shock;
            Y_SRAS[i]     = Y_pot + AS_slope * dP;
        }

        // 4) Находим равновесие после шока (минимальная разность |Y_AD_shift - Y_SRAS|)
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

        // 5) Строим блок "equilibrium"
        // Клиент ожидает:
        //    "AD":          List<Map<"x",Number>,<"y",Number>>  (x=Y, y=P)
        //    "SRAS":        List<Map<"x",Number>,<"y",Number>>  (x=Y, y=P)
        //    "LRAS":        List<Map<"x",Number>,<"y",Number>>  (x=Y_pot, y=P)
        //    "equilibrium": Map<"x",Number>,<"y",Number>       (x=eq_Y, y=eq_P)
        List<Map<String, Number>> seriesAD      = new ArrayList<>();
        List<Map<String, Number>> seriesSRAS    = new ArrayList<>();
        List<Map<String, Number>> seriesLRAS    = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            // для равновесия и визуализации AD-AS на графике x=Y, y=P
            seriesAD.add(Map.of("x", Y_AD_shift[i], "y", P[i]));
            seriesSRAS.add(Map.of("x", Y_SRAS[i],   "y", P[i]));
            seriesLRAS.add(Map.of("x", Y_pot,       "y", P[i]));
        }
        Map<String, Object> equilibriumData = new LinkedHashMap<>();
        equilibriumData.put("AD",          seriesAD);
        equilibriumData.put("SRAS",        seriesSRAS);
        equilibriumData.put("LRAS",        seriesLRAS);
        equilibriumData.put("equilibrium", Map.of("x", eq_Y_post, "y", eq_P_post));

        // 6) Строим блок "shifts"
        // Клиент ожидает:
        //    "AD"   (до):   точки AD_base,
        //    "AD2"  (после): точки AD_shift,
        //    "SRAS" (до) и "SRAS2" (после) — одинаковые,
        //    "LRAS" — вертикальная линия Y=Y_pot.
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

        // 7) Строим блок "gaps"
        // Клиент ожидает:
        //    "AD":         точки AD_shift (фактическая, после шока),
        //    "SRAS":       точки SRAS,
        //    "LRAS":       вертикала по Y=Y_pot,
        //    "potentialY": число Y_pot,
        //    "actualY":    число eq_Y_post.
        List<Map<String, Number>> seriesAD_post = new ArrayList<>();
        List<Map<String, Number>> seriesSRAS_post= new ArrayList<>();
        List<Map<String, Number>> seriesLRAS3   = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            seriesAD_post.add(Map.of("x", Y_AD_shift[i], "y", P[i]));
            seriesSRAS_post.add(Map.of("x", Y_SRAS[i],   "y", P[i]));
            seriesLRAS3.add(Map.of("x", Y_pot,           "y", P[i]));
        }
        Map<String, Object> gapsData = new LinkedHashMap<>();
        gapsData.put("AD",         seriesAD_post);
        gapsData.put("SRAS",       seriesSRAS_post);
        gapsData.put("LRAS",       seriesLRAS3);
        gapsData.put("potentialY", Y_pot);
        gapsData.put("actualY",    eq_Y_post);

        // 8) Собираем единый JSON-объект с тремя ключами: "equilibrium", "shifts", "gaps"
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("equilibrium", equilibriumData);
        allCharts.put("shifts",      shiftsData);
        allCharts.put("gaps",        gapsData);

        // 9) Сериализуем и возвращаем ModelResultDto
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
