package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Парсер результатов базовой модели спроса и предложения.
 *
 * <p>Ожидаемая структура JSON:
 * <pre>
 * {
 *   "supply_demand": {
 *     "equilibrium": {"quantity": number, "price": number},
 *     "demand": [...],
 *     "supply": [...],
 *     "consumer_surplus_area": ...,  // optional
 *     "producer_surplus_area": ...   // optional
 *   },
 *   "shift_animation": {
 *     "demand_shifts": [...],
 *     "supply_shifts": [...]
 *   }
 * }
 * </pre>
 */
public class DemandSupplyResultParser implements ResultParser {

    /**
     * Создаёт текстовое описание результатов модели спроса и предложения.
     *
     * @param json JSON строка, возвращённая сервисом
     * @return читабельный отчёт
     */
    @Override
    public String parse(String json) {
        JSONObject root = new JSONObject(json);
        JSONObject sd = root.optJSONObject("supply_demand");
        if (sd == null) return I18n.t("result.demand.no_data");

        StringBuilder sb = new StringBuilder();
        sb.append(I18n.t("result.demand.title")).append("\n\n");

        JSONObject eq = sd.optJSONObject("equilibrium");
        if (eq != null) {
            double eqQ = eq.optDouble("quantity", Double.NaN);
            double eqP = eq.optDouble("price", Double.NaN);
            sb.append(String.format(I18n.t("result.demand.equilibrium"), eqQ, eqP));
        }

        JSONArray demand = sd.optJSONArray("demand");
        JSONArray supply = sd.optJSONArray("supply");
        if (demand != null && supply != null) {
            sb.append(I18n.t("result.demand.examples_title")).append("\n");
            int points = Math.min(3, Math.min(demand.length(), supply.length()));
            for (int i = 0; i < points; i++) {
                JSONObject d = demand.getJSONObject(i);
                JSONObject s = supply.getJSONObject(i);
                sb.append(String.format(I18n.t("result.demand.example_point"),
                        d.optDouble("quantity"), d.optDouble("price"),
                        s.optDouble("quantity"), s.optDouble("price")
                ));
            }
            sb.append(I18n.t("result.demand.ellipsis")).append("\n\n");
        }

        if (sd.has("consumer_surplus_area") || sd.has("producer_surplus_area")) {
            sb.append(I18n.t("result.demand.surplus_title")).append("\n");
            if (sd.has("consumer_surplus_area")) sb.append(I18n.t("result.demand.consumer_surplus")).append("\n");
            if (sd.has("producer_surplus_area")) sb.append(I18n.t("result.demand.producer_surplus")).append("\n");
            sb.append("\n");
        }

        JSONObject shift = root.optJSONObject("shift_animation");
        if (shift != null) {
            int dShifts = shift.optJSONArray("demand_shifts") != null ?
                    shift.getJSONArray("demand_shifts").length() : 0;
            int sShifts = shift.optJSONArray("supply_shifts") != null ?
                    shift.getJSONArray("supply_shifts").length() : 0;
            if (dShifts > 1 || sShifts > 1) {
                sb.append(I18n.t("result.demand.shift_calc")).append("\n\n");
            }
        }

        sb.append(I18n.t("result.demand.summary")).append("\n");
        return sb.toString();
    }
}
