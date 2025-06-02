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
 * Обновлённый солвер для Теории потребительского выбора,
 * который формирует три блока данных:
 * 1) "indifference_curves"  — семейство кривых безразличия + бюджетная линия;
 * 2) "optimum_map"         — одна кривая безразличия + бюджетная линия + точка оптимума;
 * 3) "income_substitution" — данные для построения эффекта дохода и замещения,
 *    при условии, что цена Py повышается на 10%.
 */
@Component
public class ConsumerChoiceSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        // 1) Читаем входные параметры
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double I     = (Double) ParameterTypeConverter.fromString(paramMap.get("I"),     "double"); // Доход
        double Px    = (Double) ParameterTypeConverter.fromString(paramMap.get("Px"),    "double"); // Цена X
        double Py    = (Double) ParameterTypeConverter.fromString(paramMap.get("Py"),    "double"); // Цена Y
        double alpha = (Double) ParameterTypeConverter.fromString(paramMap.get("alpha"), "double"); // Коэфф. предпочтения
        double U     = (Double) ParameterTypeConverter.fromString(paramMap.get("U"),     "double"); // Уровень полезности

        // 2) Рассчитываем первоначальный оптимум (Cobb–Douglas):
        //    X* = alpha * I / Px,    Y* = (1 - alpha) * I / Py
        double Xopt = alpha * I / Px;
        double Yopt = (1 - alpha) * I / Py;

        // 3) Строим бюджетную линию до изменения цены:
        //    I = Px·X + Py·Y  =>  Y = (I - Px·X)/Py
        int N = 40;
        double Xmax = I / Px * 1.2; // чуть больше, чтобы линия немного выступала за точку (Xopt,Yopt)
        double[] Xs = ChartDataUtil.range(0, Xmax, N);
        double[] Ybl = new double[N];
        for (int i = 0; i < N; i++) {
            Ybl[i] = (I - Px * Xs[i]) / Py;
        }
        List<Map<String, Double>> budgetPoints = ChartDataUtil.pointsList(Xs, Ybl);

        // 4) Строим кривую безразличия для одного уровня U при исходных ценах:
        //    Условие: U = X^alpha * Y^(1-alpha)  =>  Y = (U / X^alpha)^(1/(1-alpha))
        double[] YindiffBase = new double[N];
        for (int i = 0; i < N; i++) {
            if (Xs[i] > 0 && alpha < 1.0) {
                YindiffBase[i] = Math.pow(U / Math.pow(Xs[i], alpha), 1.0 / (1.0 - alpha));
            } else {
                YindiffBase[i] = 0.0;
            }
        }
        List<Map<String, Double>> indifferenceCurve = ChartDataUtil.pointsList(Xs, YindiffBase);

        // 5) Для семейства кривых безразличия возьмём несколько уровней U,
        //    например: U, U*0.8 и U*0.6 (чтобы показать «семейство»).
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

        // 6) Теперь моделируем «income_substitution» при увеличении Py на 10%:
        double Py_new = Py * 1.10; // новая цена Y

        // 6.1) Вычисляем новый оптимум (X₂, Y₂) при тех же I, но при ценах Px, Py_new:
        double Xopt2 = alpha * I / Px;
        double Yopt2 = (1 - alpha) * I / Py_new;

        // 6.2) Находим компенсационный (хиксианский) оптимум (X_h, Y_h),
        //      при котором потребитель достигает уровня U, но цены (Px, Py_new) и
        //      минимизирует расходы. Для Cobb-Douglas хиксианский:
        //      e = минимальные расходы => X_h = alpha * e / Px,  Y_h = (1-alpha) * e / Py_new,
        //      и U = X_h^alpha * Y_h^(1-alpha)  =>  e = (Px^alpha)*(Py_new^(1-alpha))*U/(alpha^alpha * (1-alpha)^(1-alpha))
        double eH = Math.pow(Px, alpha) * Math.pow(Py_new, 1.0 - alpha) * U
                / (Math.pow(alpha, alpha) * Math.pow(1.0 - alpha, 1.0 - alpha));
        double Xh = alpha * eH / Px;
        double Yh = (1.0 - alpha) * eH / Py_new;

        // 7) Формируем блок "indifference_curves"
        // Клиент ожидает:
        //   - "indifference_curves": List<List<Map<String, Number>>> (каждая внутренняя — отдельная кривая)
        //   - "budget":             List<Map<String, Number>> (x, y)
        Map<String, Object> indiffCurvesData = new LinkedHashMap<>();
        indiffCurvesData.put("indifference_curves", indifferenceFamily);
        indiffCurvesData.put("budget", budgetPoints);

        // 8) Формируем блок "optimum_map"
        // Клиент ожидает:
        //   - "indifference_curve": List<Map<String, Number>>
        //   - "budget":             List<Map<String, Number>>
        //   - "optimum":            Map<String, Number> (x, y)
        Map<String, Object> optimumMapData = new LinkedHashMap<>();
        optimumMapData.put("indifference_curve", indifferenceCurve);
        optimumMapData.put("budget", budgetPoints);
        optimumMapData.put("optimum", Map.of(
                "x", Xopt,
                "y", Yopt
        ));

        // 9) Формируем блок "income_substitution"
        // Клиент ожидает:
        //   - "before":       List<Map<String, Number>> (кривая безразличия до изменения)
        //   - "after":        List<Map<String, Number>> (кривая безразличия после изменения)
        //   - "substitution": List<Map<String, Number>> (отрезок от (Xopt, Yopt) до (Xh, Yh))
        //   - "income":       List<Map<String, Number>> (отрезок от (Xh, Yh) до (Xopt2, Yopt2))
        // «Кривая безразличия после изменения» — строится при том же U, но ценах (Px, Py_new):
        double[] YindiffNew = new double[N];
        for (int i = 0; i < N; i++) {
            if (Xs[i] > 0 && alpha < 1.0) {
                YindiffNew[i] = Math.pow(U / Math.pow(Xs[i], alpha), 1.0 / (1.0 - alpha)) * (Py / Py_new);
                // упрощённая шкала, чтобы кривая «сместилась» вниз из-за более высокой Py
            } else {
                YindiffNew[i] = 0.0;
            }
        }
        List<Map<String, Double>> indiffAfter = ChartDataUtil.pointsList(Xs, YindiffNew);

        // «Отрезок substitution»: достаточно двух точек
        List<Map<String, Number>> substitutionSegment = List.of(
                Map.of("x", Xopt, "y", Yopt),
                Map.of("x", Xh,    "y", Yh)
        );
        // «Отрезок income»: от компенсационного до нового оптимума
        List<Map<String, Number>> incomeSegment = List.of(
                Map.of("x", Xh,    "y", Yh),
                Map.of("x", Xopt2, "y", Yopt2)
        );

        Map<String, Object> incomeSubstData = new LinkedHashMap<>();
        incomeSubstData.put("before",       indifferenceCurve);
        incomeSubstData.put("after",        indiffAfter);
        incomeSubstData.put("substitution", substitutionSegment);
        incomeSubstData.put("income",       incomeSegment);

        // 10) Собираем единый JSON-объект с тремя ключами
        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("indifference_curves", indiffCurvesData);
        allCharts.put("optimum_map",         optimumMapData);
        allCharts.put("income_substitution", incomeSubstData);

        // 11) Упаковываем в DTO и возвращаем
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
