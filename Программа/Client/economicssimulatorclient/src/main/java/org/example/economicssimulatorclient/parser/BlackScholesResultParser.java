package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class BlackScholesResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // Поверхность цен call-опциона
        JSONObject surfaceObj = root.optJSONObject("surface");
        if (surfaceObj != null) {
            JSONArray surfaceArr = surfaceObj.optJSONArray("surface");
            if (surfaceArr != null && surfaceArr.length() > 0) {
                JSONArray SRow = surfaceArr.getJSONArray(0);
                JSONArray CCol = surfaceArr.getJSONArray(surfaceArr.length() - 1);
                JSONObject minC = SRow.getJSONObject(0);
                JSONObject maxC = SRow.getJSONObject(SRow.length() - 1);
                double Smin = minC.optDouble("S", Double.NaN);
                double Smax = maxC.optDouble("S", Double.NaN);
                double Cmin = minC.optDouble("C", Double.NaN);
                double Cmax = maxC.optDouble("C", Double.NaN);

                sb.append("Цены call-опциона по Black-Scholes:\n");
                sb.append(String.format("  • При цене актива от %.2f до %.2f опцион стоит от %.2f до %.2f\n",
                        Smin, Smax, Cmin, Cmax));
            }
        }

        // Время жизни опциона и распад
        JSONObject decayObj = root.optJSONObject("decay");
        if (decayObj != null) {
            JSONArray decayArr = decayObj.optJSONArray("decay");
            if (decayArr != null && decayArr.length() > 0) {
                JSONObject first = decayArr.getJSONObject(0);
                JSONObject last = decayArr.getJSONObject(decayArr.length() - 1);
                double Tmax = first.optDouble("T", Double.NaN);
                double Tmin = last.optDouble("T", Double.NaN);
                double Cstart = first.optDouble("C", Double.NaN);
                double Cend = last.optDouble("C", Double.NaN);
                sb.append("\nВлияние времени до экспирации:\n");
                sb.append(String.format("  • Цена опциона падает от %.2f до %.2f при уменьшении времени с %.2f до %.2f\n",
                        Cstart, Cend, Tmax, Tmin));
            }
        }

        // Греки: delta, gamma, vega, theta, rho
        JSONObject greeksObj = root.optJSONObject("greeks");
        if (greeksObj != null) {
            JSONObject greeks = greeksObj.optJSONObject("greeks");
            if (greeks != null) {
                sb.append("\nГреки (чувствительность):\n");
                appendGreek(sb, greeks, "delta", "Дельта");
                appendGreek(sb, greeks, "gamma", "Гамма");
                appendGreek(sb, greeks, "vega",  "Вега");
                appendGreek(sb, greeks, "theta", "Тета");
                appendGreek(sb, greeks, "rho",   "Ро");
            }
        }

        if (sb.length() == 0) {
            sb.append("Нет данных для отображения (Black-Scholes).");
        }

        return sb.toString().trim();
    }

    private void appendGreek(StringBuilder sb, JSONObject greeks, String key, String label) {
        JSONArray arr = greeks.optJSONArray(key);
        if (arr != null && arr.length() > 0) {
            double minVal = arr.getJSONObject(0).optDouble("value", Double.NaN);
            double maxVal = arr.getJSONObject(arr.length() - 1).optDouble("value", Double.NaN);
            sb.append(String.format("  • %s: от %.4f до %.4f\n", label, minVal, maxVal));
        }
    }
}
