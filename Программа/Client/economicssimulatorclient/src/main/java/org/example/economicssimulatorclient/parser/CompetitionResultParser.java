package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class CompetitionResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // 1. Кривые прибыли для конкуренции и монополии
        JSONObject profitCurves = root.optJSONObject("profit_curves");
        if (profitCurves != null) {
            JSONArray competition = profitCurves.optJSONArray("competition");
            JSONArray monopoly = profitCurves.optJSONArray("monopoly");
            if (competition != null && monopoly != null && competition.length() > 0 && monopoly.length() > 0) {
                JSONObject compMax = competition.getJSONObject(competition.length() - 1);
                JSONObject monoMax = monopoly.getJSONObject(0);
                sb.append("Кривые прибыли:\n");
                sb.append(String.format("  • Конкуренция: макс. прибыль %.2f при выпуске %.2f\n",
                        compMax.optDouble("profit", Double.NaN), compMax.optDouble("quantity", Double.NaN)));
                sb.append(String.format("  • Монополия: макс. прибыль %.2f при выпуске %.2f\n",
                        monoMax.optDouble("profit", Double.NaN), monoMax.optDouble("quantity", Double.NaN)));
            }
        }

        // 2. Гистограмма сравнения
        JSONObject hist = root.optJSONObject("comparison_hist");
        if (hist != null) {
            JSONArray categories = hist.optJSONArray("categories");
            JSONArray competition = hist.optJSONArray("competition");
            JSONArray monopoly = hist.optJSONArray("monopoly");
            sb.append("\nСравнение конкуренции и монополии (гистограмма):\n");
            if (categories != null && competition != null && monopoly != null) {
                for (int i = 0; i < categories.length(); i++) {
                    String cat = categories.optString(i, "");
                    double compVal = competition.optDouble(i, Double.NaN);
                    double monoVal = monopoly.optDouble(i, Double.NaN);
                    sb.append(String.format("  • %s: конкуренция %.2f, монополия %.2f\n", cat, compVal, monoVal));
                }
            }
        }

        // 3. Мертвый груз (deadweight area)
        JSONObject deadweight = root.optJSONObject("deadweight_area");
        if (deadweight != null) {
            double monopolyQ = deadweight.optDouble("monopolyQ", Double.NaN);
            double competitionQ = deadweight.optDouble("competitionQ", Double.NaN);
            sb.append("\nОбласть мертвого груза (deadweight loss):\n");
            sb.append(String.format("  • Объем при монополии: %.2f\n", monopolyQ));
            sb.append(String.format("  • Объем при конкуренции: %.2f\n", competitionQ));
            JSONArray demand = deadweight.optJSONArray("demand");
            JSONArray supply = deadweight.optJSONArray("supply");
            if (demand != null && supply != null && demand.length() > 0 && supply.length() > 0) {
                JSONObject dFirst = demand.getJSONObject(0);
                JSONObject sFirst = supply.getJSONObject(0);
                sb.append(String.format("  • Начальная цена: спрос %.2f, предложение %.2f\n",
                        dFirst.optDouble("price", Double.NaN), sFirst.optDouble("price", Double.NaN)));
            }
        }

        if (sb.length() == 0) {
            sb.append("Нет данных для Competition vs Monopoly.");
        }

        return sb.toString().trim();
    }
}
