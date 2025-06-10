package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class ElasticityResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        JSONObject root = new JSONObject(json);
        StringBuilder sb = new StringBuilder();
        sb.append("Результаты модели эластичности:\n\n");

        // 1. Эластичность спроса (пример расчёта)
        JSONObject curves = root.optJSONObject("elasticity_curves");
        if (curves != null) {
            JSONArray elastic = curves.optJSONArray("elastic");
            if (elastic != null && elastic.length() >= 2) {
                JSONObject point1 = elastic.getJSONObject(0);
                JSONObject point2 = elastic.getJSONObject(1);
                double q1 = point1.optDouble("quantity");
                double p1 = point1.optDouble("price");
                double q2 = point2.optDouble("quantity");
                double p2 = point2.optDouble("price");
                sb.append(String.format(
                        "• Пример изменения спроса:\n  - Цена %.2f → %.2f\n  - Количество %.2f → %.2f\n\n",
                        p1, p2, q1, q2
                ));
            }
        }

        // 2. Выручка по категориям (если есть)
        JSONObject revenue = root.optJSONObject("revenue_bars");
        if (revenue != null) {
            JSONArray categories = revenue.optJSONArray("categories");
            JSONArray revenue0 = revenue.optJSONArray("revenue0");
            JSONArray revenue1 = revenue.optJSONArray("revenue1");
            if (categories != null && categories.length() > 0) {
                sb.append("• Доход по категориям:\n");
                // Если есть только один товар
                for (int i = 0; i < categories.length(); i++) {
                    String cat = categories.getString(i);
                    double rev0 = revenue0 != null && revenue0.length() > i ? revenue0.optDouble(i) : Double.NaN;
                    double rev1 = revenue1 != null && revenue1.length() > i ? revenue1.optDouble(i) : Double.NaN;
                    if (!Double.isNaN(rev0) && !Double.isNaN(rev1)) {
                        sb.append(String.format("  - %s: до изменения = %.2f, после = %.2f\n", cat, rev0, rev1));
                    } else if (!Double.isNaN(rev0)) {
                        sb.append(String.format("  - %s: доход = %.2f\n", cat, rev0));
                    }
                }
                sb.append("\n");
            }
        }

        // 3. Значения эластичности
        JSONObject heatmap = root.optJSONObject("elasticity_heatmap");
        if (heatmap != null) {
            JSONArray cats = heatmap.optJSONArray("categories");
            JSONArray elasticity = heatmap.optJSONArray("elasticity");
            if (cats != null && elasticity != null) {
                sb.append("• Эластичность по категориям:\n");
                for (int i = 0; i < cats.length() && i < elasticity.length(); i++) {
                    String cat = cats.getString(i);
                    double el = elasticity.optDouble(i, Double.NaN);
                    sb.append(String.format("  - %s: эластичность = %.3f\n", cat, el));
                }
                sb.append("\n");
            }
        }

        // Итоговое пояснение
        sb.append("Число меньше -1: эластичный спрос.\n");
        sb.append("Число от -1 до 0: неэластичный спрос.\n");

        return sb.toString();
    }
}
