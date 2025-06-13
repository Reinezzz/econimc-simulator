package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Парсер результатов сравнения совершенной конкуренции и монополии.
 *
 * <p>Ожидаемая структура JSON:
 * <pre>
 * {
 *   "profit_curves": {
 *     "competition": [...],
 *     "monopoly": [...]
 *   },
 *   "comparison_hist": {
 *     "categories": [...],
 *     "competition": [...],
 *     "monopoly": [...]
 *   },
 *   "deadweight_area": {
 *     "monopolyQ": number,
 *     "competitionQ": number,
 *     "demand": [...],
 *     "supply": [...]
 *   }
 * }
 * </pre>
 */
public class CompetitionResultParser implements ResultParser {

    /**
     * Обрабатывает JSON сравнения конкуренции и монополии, возвращая
     * текстовое резюме расчётов.
     *
     * @param json строка JSON от сервера
     * @return человекочитаемое описание
     */
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        JSONObject profitCurves = root.optJSONObject("profit_curves");
        if (profitCurves != null) {
            JSONArray competition = profitCurves.optJSONArray("competition");
            JSONArray monopoly = profitCurves.optJSONArray("monopoly");
            if (competition != null && monopoly != null && !competition.isEmpty() && !monopoly.isEmpty()) {
                JSONObject compMax = competition.getJSONObject(competition.length() - 1);
                JSONObject monoMax = monopoly.getJSONObject(0);
                sb.append(I18n.t("result.competition.profit_title")).append("\n");
                sb.append(String.format(I18n.t("result.competition.profit_competition"),
                        compMax.optDouble("profit", Double.NaN), compMax.optDouble("quantity", Double.NaN)));
                sb.append(String.format(I18n.t("result.competition.profit_monopoly"),
                        monoMax.optDouble("profit", Double.NaN), monoMax.optDouble("quantity", Double.NaN)));
            }
        }

        JSONObject hist = root.optJSONObject("comparison_hist");
        if (hist != null) {
            JSONArray categories = hist.optJSONArray("categories");
            JSONArray competition = hist.optJSONArray("competition");
            JSONArray monopoly = hist.optJSONArray("monopoly");
            sb.append("\n").append(I18n.t("result.competition.comparison_title")).append("\n");
            if (categories != null && competition != null && monopoly != null) {
                for (int i = 0; i < categories.length(); i++) {
                    String cat = categories.optString(i, "");
                    double compVal = competition.optDouble(i, Double.NaN);
                    double monoVal = monopoly.optDouble(i, Double.NaN);
                    sb.append(String.format(I18n.t("result.competition.comparison_item"), cat, compVal, monoVal));
                }
            }
        }

        JSONObject deadweight = root.optJSONObject("deadweight_area");
        if (deadweight != null) {
            double monopolyQ = deadweight.optDouble("monopolyQ", Double.NaN);
            double competitionQ = deadweight.optDouble("competitionQ", Double.NaN);
            sb.append("\n").append(I18n.t("result.competition.deadweight_title")).append("\n");
            sb.append(String.format(I18n.t("result.competition.deadweight_monopoly"), monopolyQ));
            sb.append(String.format(I18n.t("result.competition.deadweight_competition"), competitionQ));
            JSONArray demand = deadweight.optJSONArray("demand");
            JSONArray supply = deadweight.optJSONArray("supply");
            if (demand != null && supply != null && !demand.isEmpty() && !supply.isEmpty()) {
                JSONObject dFirst = demand.getJSONObject(0);
                JSONObject sFirst = supply.getJSONObject(0);
                sb.append(String.format(I18n.t("result.competition.deadweight_start"),
                        dFirst.optDouble("price", Double.NaN), sFirst.optDouble("price", Double.NaN)));
            }
        }

        if (sb.isEmpty()) {
            sb.append(I18n.t("result.competition.no_data"));
        }

        return sb.toString().trim();
    }
}
