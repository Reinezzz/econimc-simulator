package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
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
            sb.append(I18n.t("result.adas.title")).append("\n");
            JSONObject eqPoint = equilibriumBlock.optJSONObject("equilibrium");
            if (eqPoint != null) {
                double x = eqPoint.optDouble("x", Double.NaN);
                double y = eqPoint.optDouble("y", Double.NaN);
                sb.append(String.format(I18n.t("result.adas.equilibrium_point"), x, y));
            }
            // Факультативно: можно вывести координаты LRAS
            JSONArray lras = equilibriumBlock.optJSONArray("LRAS");
            if (lras != null && lras.length() > 0) {
                JSONObject lrasPt = lras.getJSONObject(0);
                sb.append(String.format(I18n.t("result.adas.lras"), lrasPt.optDouble("x", Double.NaN)));
            }
        }

        // --- Сдвиги кривых ---
        JSONObject shifts = root.optJSONObject("shifts");
        if (shifts != null) {
            sb.append("\n").append(I18n.t("result.adas.shifts_title")).append("\n");
            // Сдвиг AD
            JSONArray ad2 = shifts.optJSONArray("AD2");
            if (ad2 != null && ad2.length() > 0) {
                JSONObject ad2first = ad2.getJSONObject(0);
                sb.append(String.format(I18n.t("result.adas.ad2_shift"), ad2first.optDouble("x", Double.NaN), ad2first.optDouble("y", Double.NaN)));
            }
            // Сдвиг SRAS
            JSONArray sras2 = shifts.optJSONArray("SRAS2");
            if (sras2 != null && sras2.length() > 0) {
                JSONObject sras2first = sras2.getJSONObject(0);
                sb.append(String.format(I18n.t("result.adas.sras2_shift"), sras2first.optDouble("x", Double.NaN), sras2first.optDouble("y", Double.NaN)));
            }
        }

        // --- Разрывы (gaps) ---
        JSONObject gaps = root.optJSONObject("gaps");
        if (gaps != null) {
            sb.append("\n").append(I18n.t("result.adas.gaps_title")).append("\n");
            double potentialY = gaps.optDouble("potentialY", Double.NaN);
            double actualY = gaps.optDouble("actualY", Double.NaN);
            sb.append(String.format(I18n.t("result.adas.potential_output"), potentialY));
            sb.append(String.format(I18n.t("result.adas.actual_output"), actualY));
            double gap = actualY - potentialY;
            sb.append(String.format(I18n.t("result.adas.gap"), gap));
        }

        if (sb.length() == 0) {
            sb.append(I18n.t("result.adas.no_data"));
        }

        return sb.toString().trim();
    }
}
