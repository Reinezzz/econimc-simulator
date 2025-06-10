package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class ADASResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // --- Равновесие ---
        JSONObject equilibriumBlock = root.optJSONObject("equilibrium");
        if (equilibriumBlock != null) {
            sb.append("Классическая модель AD-AS:\n");
            JSONObject eqPoint = equilibriumBlock.optJSONObject("equilibrium");
            if (eqPoint != null) {
                double x = eqPoint.optDouble("x", Double.NaN);
                double y = eqPoint.optDouble("y", Double.NaN);
                sb.append(String.format("  • Точка равновесия: выпуск %.2f, уровень цен %.2f\n", x, y));
            }
            // Факультативно: можно вывести координаты LRAS
            JSONArray lras = equilibriumBlock.optJSONArray("LRAS");
            if (lras != null && lras.length() > 0) {
                JSONObject lrasPt = lras.getJSONObject(0);
                sb.append(String.format("  • Долгосрочный потенциальный выпуск: %.2f\n", lrasPt.optDouble("x", Double.NaN)));
            }
        }

        // --- Сдвиги кривых ---
        JSONObject shifts = root.optJSONObject("shifts");
        if (shifts != null) {
            sb.append("\nСценарии со сдвигами:\n");
            // Сдвиг AD
            JSONArray ad2 = shifts.optJSONArray("AD2");
            if (ad2 != null && ad2.length() > 0) {
                JSONObject ad2first = ad2.getJSONObject(0);
                sb.append(String.format("  • Сдвиг AD2: старт x=%.2f, y=%.2f\n", ad2first.optDouble("x", Double.NaN), ad2first.optDouble("y", Double.NaN)));
            }
            // Сдвиг SRAS
            JSONArray sras2 = shifts.optJSONArray("SRAS2");
            if (sras2 != null && sras2.length() > 0) {
                JSONObject sras2first = sras2.getJSONObject(0);
                sb.append(String.format("  • Сдвиг SRAS2: старт x=%.2f, y=%.2f\n", sras2first.optDouble("x", Double.NaN), sras2first.optDouble("y", Double.NaN)));
            }
        }

        // --- Разрывы (gaps) ---
        JSONObject gaps = root.optJSONObject("gaps");
        if (gaps != null) {
            sb.append("\nОтклонения от потенциального выпуска:\n");
            double potentialY = gaps.optDouble("potentialY", Double.NaN);
            double actualY = gaps.optDouble("actualY", Double.NaN);
            sb.append(String.format("  • Потенциальный выпуск: %.2f\n", potentialY));
            sb.append(String.format("  • Фактический выпуск: %.2f\n", actualY));
            double gap = actualY - potentialY;
            sb.append(String.format("  • Отклонение (gap): %.2f\n", gap));
        }

        if (sb.length() == 0) {
            sb.append("Нет данных для модели AD-AS.");
        }

        return sb.toString().trim();
    }
}
