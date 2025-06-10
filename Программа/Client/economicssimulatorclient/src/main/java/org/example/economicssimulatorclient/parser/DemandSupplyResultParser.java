package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class DemandSupplyResultParser implements ResultParser {

    @Override
    public String parse(String json) {
        JSONObject root = new JSONObject(json);
        JSONObject sd = root.optJSONObject("supply_demand");
        if (sd == null) return "Нет данных по модели спроса и предложения.";

        StringBuilder sb = new StringBuilder();
        sb.append("Результаты модели \"Спрос и предложение\":\n\n");

        // 1. Равновесие рынка
        JSONObject eq = sd.optJSONObject("equilibrium");
        if (eq != null) {
            double eqQ = eq.optDouble("quantity", Double.NaN);
            double eqP = eq.optDouble("price", Double.NaN);
            sb.append(String.format(
                    "• Равновесие рынка:\n  - Количество: %.2f\n  - Цена: %.2f\n\n", eqQ, eqP
            ));
        }

        // 2. Пример точек спроса и предложения
        JSONArray demand = sd.optJSONArray("demand");
        JSONArray supply = sd.optJSONArray("supply");
        if (demand != null && supply != null) {
            sb.append("• Примеры точек:\n");
            int points = Math.min(3, Math.min(demand.length(), supply.length()));
            for (int i = 0; i < points; i++) {
                JSONObject d = demand.getJSONObject(i);
                JSONObject s = supply.getJSONObject(i);
                sb.append(String.format(
                        "  - Спрос: Q=%.2f, P=%.2f | Предложение: Q=%.2f, P=%.2f\n",
                        d.optDouble("quantity"), d.optDouble("price"),
                        s.optDouble("quantity"), s.optDouble("price")
                ));
            }
            sb.append("  ...\n\n");
        }

        // 3. Излишки
        if (sd.has("consumer_surplus_area") || sd.has("producer_surplus_area")) {
            sb.append("• Рыночные излишки (графически отображены на графике):\n");
            if (sd.has("consumer_surplus_area")) sb.append("  - Излишек потребителя\n");
            if (sd.has("producer_surplus_area")) sb.append("  - Излишек производителя\n");
            sb.append("\n");
        }

        // 4. Объяснение сдвигов (если есть shift_animation)
        JSONObject shift = root.optJSONObject("shift_animation");
        if (shift != null) {
            int dShifts = shift.optJSONArray("demand_shifts") != null ?
                    shift.getJSONArray("demand_shifts").length() : 0;
            int sShifts = shift.optJSONArray("supply_shifts") != null ?
                    shift.getJSONArray("supply_shifts").length() : 0;
            if (dShifts > 1 || sShifts > 1) {
                sb.append("• Были рассчитаны сдвиги спроса/предложения и новые равновесные точки.\n\n");
            }
        }

        // 5. Краткое summary
        sb.append("Вы можете подробнее рассмотреть кривые спроса и предложения на графике. В таблице выше представлены ключевые расчетные значения: равновесие, примеры точек, а также рыночные излишки.\n");

        return sb.toString();
    }
}
