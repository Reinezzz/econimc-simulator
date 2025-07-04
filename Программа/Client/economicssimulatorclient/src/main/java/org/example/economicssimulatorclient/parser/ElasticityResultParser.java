package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Парсер результатов модели ценовой эластичности спроса.
 *
 * <p>Ожидаемая структура JSON:
 * <pre>
 * {
 *   "elasticity_curves": {
 *     "elastic": [{"quantity": number, "price": number}, ...]
 *   },
 *   "revenue_bars": {
 *     "categories": [...],
 *     "revenue0": [...],
 *     "revenue1": [...]
 *   },
 *   "elasticity_heatmap": {
 *     "categories": [...],
 *     "elasticity": [...]
 *   }
 * }
 * </pre>
 */
public class ElasticityResultParser implements ResultParser {

    /**
     * Разбирает JSON и формирует текстовое описание результатов модели
     * эластичности спроса.
     *
     * @param json исходный JSON от сервера
     * @return форматированный отчёт о расчётах
     */
    @Override
    public String parse(String json) {
        JSONObject root = new JSONObject(json);
        StringBuilder sb = new StringBuilder();
        sb.append(I18n.t("result.elasticity.title")).append("\n\n");

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
                sb.append(String.format(I18n.t("result.elasticity.example"), p1, p2, q1, q2));
            }
        }

        JSONObject revenue = root.optJSONObject("revenue_bars");
        if (revenue != null) {
            JSONArray categories = revenue.optJSONArray("categories");
            JSONArray revenue0 = revenue.optJSONArray("revenue0");
            JSONArray revenue1 = revenue.optJSONArray("revenue1");
            if (categories != null && !categories.isEmpty()) {
                sb.append(I18n.t("result.elasticity.revenue_title")).append("\n");
                for (int i = 0; i < categories.length(); i++) {
                    String cat = categories.getString(i);
                    double rev0 = revenue0 != null && revenue0.length() > i ? revenue0.optDouble(i) : Double.NaN;
                    double rev1 = revenue1 != null && revenue1.length() > i ? revenue1.optDouble(i) : Double.NaN;
                    if (!Double.isNaN(rev0) && !Double.isNaN(rev1)) {
                        sb.append(String.format(I18n.t("result.elasticity.revenue_before_after"), cat, rev0, rev1));
                    } else if (!Double.isNaN(rev0)) {
                        sb.append(String.format(I18n.t("result.elasticity.revenue_single"), cat, rev0));
                    }
                }
                sb.append("\n");
            }
        }

        JSONObject heatmap = root.optJSONObject("elasticity_heatmap");
        if (heatmap != null) {
            JSONArray cats = heatmap.optJSONArray("categories");
            JSONArray elasticity = heatmap.optJSONArray("elasticity");
            if (cats != null && elasticity != null) {
                sb.append(I18n.t("result.elasticity.elasticity_title")).append("\n");
                for (int i = 0; i < cats.length() && i < elasticity.length(); i++) {
                    String cat = cats.getString(i);
                    double el = elasticity.optDouble(i, Double.NaN);
                    sb.append(String.format(I18n.t("result.elasticity.elasticity_item"), cat, el));
                }
                sb.append("\n");
            }
        }

        sb.append(I18n.t("result.elasticity.elastic_range_elastic")).append("\n");
        sb.append(I18n.t("result.elasticity.elastic_range_inelastic")).append("\n");

        return sb.toString();
    }
}
