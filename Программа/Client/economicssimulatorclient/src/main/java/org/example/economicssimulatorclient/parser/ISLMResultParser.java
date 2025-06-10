package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class ISLMResultParser implements ResultParser {

    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        JSONObject islm = root.optJSONObject("is_lm");
        if (islm != null) {
            // Равновесие
            JSONObject equilibrium = islm.optJSONObject("equilibrium");
            if (equilibrium != null) {
                sb.append("Равновесие IS-LM:\n");
                sb.append(String.format("  • Ставка: %.4f\n", equilibrium.optDouble("rate")));
                sb.append(String.format("  • Доход: %.2f\n", equilibrium.optDouble("income")));
                sb.append("\n");
            }

            // Диапазон доходов/ставок по IS/LM (выводим крайние значения)
            JSONArray isCurve = islm.optJSONArray("IS");
            JSONArray lmCurve = islm.optJSONArray("LM");
            if (isCurve != null && isCurve.length() > 0 && lmCurve != null && lmCurve.length() > 0) {
                JSONObject isStart = isCurve.getJSONObject(0);
                JSONObject isEnd = isCurve.getJSONObject(isCurve.length() - 1);
                JSONObject lmStart = lmCurve.getJSONObject(0);
                JSONObject lmEnd = lmCurve.getJSONObject(lmCurve.length() - 1);

                sb.append("Кривая IS:\n");
                sb.append(String.format("  • Начало: ставка %.4f, доход %.2f\n", isStart.optDouble("rate"), isStart.optDouble("income")));
                sb.append(String.format("  • Конец:  ставка %.4f, доход %.2f\n", isEnd.optDouble("rate"), isEnd.optDouble("income")));
                sb.append("\n");

                sb.append("Кривая LM:\n");
                sb.append(String.format("  • Начало: ставка %.4f, доход %.2f\n", lmStart.optDouble("rate"), lmStart.optDouble("income")));
                sb.append(String.format("  • Конец:  ставка %.4f, доход %.2f\n", lmEnd.optDouble("rate"), lmEnd.optDouble("income")));
                sb.append("\n");
            }
        }

        // Timeseries: политика во времени
        JSONObject timeseries = root.optJSONObject("timeseries");
        if (timeseries != null) {
            JSONArray policy = timeseries.optJSONArray("policy");
            if (policy != null && policy.length() > 0) {
                double startIncome = policy.getJSONObject(0).optDouble("income");
                double endIncome = policy.getJSONObject(policy.length() - 1).optDouble("income");
                sb.append("Динамика дохода под действием политики:\n");
                sb.append(String.format("  • Начало: %.2f\n", startIncome));
                sb.append(String.format("  • Конец:  %.2f\n", endIncome));
                sb.append("\n");
            }
        }

        // Поверхность (если есть несколько срезов)
        JSONObject surface = root.optJSONObject("surface");
        if (surface != null) {
            JSONArray slices = surface.optJSONArray("slices");
            if (slices != null && slices.length() > 0) {
                sb.append("Диапазон поверхностей IS-LM:\n");
                for (int i = 0; i < slices.length(); i++) {
                    JSONArray slice = slices.getJSONArray(i);
                    if (slice.length() > 0) {
                        JSONObject start = slice.getJSONObject(0);
                        JSONObject end = slice.getJSONObject(slice.length() - 1);
                        sb.append(String.format("  • Срез %d: от (%.4f, %.2f) до (%.4f, %.2f)\n",
                                i + 1,
                                start.optDouble("rate"), start.optDouble("income"),
                                end.optDouble("rate"), end.optDouble("income")));
                    }
                }
                sb.append("\n");
            }
        }

        // Если ничего не извлечено, выводим предупреждение
        if (sb.length() == 0) {
            sb.append("Нет данных для отображения (IS-LM).");
        }

        return sb.toString().trim();
    }
}
