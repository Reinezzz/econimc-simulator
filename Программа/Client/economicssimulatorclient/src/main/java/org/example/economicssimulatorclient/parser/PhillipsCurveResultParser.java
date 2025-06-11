package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

public class PhillipsCurveResultParser implements ResultParser {

    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);
        sb.append(I18n.t("result.phillips.title")).append("\n");

        JSONObject scatter = root.optJSONObject("scatter");
        if (scatter != null) {
            JSONArray points = scatter.optJSONArray("points");
            if (points != null && !points.isEmpty()) {
                JSONObject p0 = points.getJSONObject(0);
                JSONObject p1 = points.getJSONObject(points.length() - 1);

                double infMin = p0.optDouble("inflation");
                double infMax = p1.optDouble("inflation");
                double unMin = p0.optDouble("unemployment");
                double unMax = p1.optDouble("unemployment");

                sb.append(String.format(I18n.t("result.phillips.inflation_range"), infMin, infMax));
                sb.append(String.format(I18n.t("result.phillips.unemployment_range"), unMin, unMax));
            }
        }

        JSONObject timeseries = root.optJSONObject("timeseries");
        if (timeseries != null) {
            JSONArray infArr = timeseries.optJSONArray("inflation");
            JSONArray unArr = timeseries.optJSONArray("unemployment");
            if (infArr != null && !infArr.isEmpty() && unArr != null && !unArr.isEmpty()) {
                JSONObject infStart = infArr.getJSONObject(0);
                JSONObject infEnd = infArr.getJSONObject(infArr.length() - 1);
                JSONObject unStart = unArr.getJSONObject(0);
                JSONObject unEnd = unArr.getJSONObject(unArr.length() - 1);

                sb.append("\n").append(I18n.t("result.phillips.dynamics_title")).append("\n");
                sb.append(String.format(I18n.t("result.phillips.dynamics_start"),
                        infStart.optDouble("inflation"), unStart.optDouble("unemployment")));
                sb.append(String.format(I18n.t("result.phillips.dynamics_end"),
                        infEnd.optDouble("inflation"), unEnd.optDouble("unemployment")));
            }
        }

        JSONObject loops = root.optJSONObject("loops");
        if (loops != null) {
            JSONArray shortRun = loops.optJSONArray("short_run");
            JSONArray longRun = loops.optJSONArray("long_run");
            if (shortRun != null && !shortRun.isEmpty()) {
                JSONObject p0 = shortRun.getJSONObject(0);
                JSONObject p1 = shortRun.getJSONObject(shortRun.length() - 1);
                sb.append("\n").append(I18n.t("result.phillips.short_run_title")).append("\n");
                sb.append(String.format(I18n.t("result.phillips.short_run_range"),
                        p0.optDouble("inflation"), p1.optDouble("inflation"),
                        p0.optDouble("unemployment"), p1.optDouble("unemployment")
                ));
            }
            if (longRun != null && !longRun.isEmpty()) {
                double lrInfl = longRun.getJSONObject(0).optDouble("inflation");
                JSONObject p0 = longRun.getJSONObject(0);
                JSONObject p1 = longRun.getJSONObject(longRun.length() - 1);
                sb.append(I18n.t("result.phillips.long_run_title")).append("\n");
                sb.append(String.format(I18n.t("result.phillips.long_run_desc"),
                        lrInfl, p0.optDouble("unemployment"), p1.optDouble("unemployment")
                ));
            }
        }

        if (sb.isEmpty()) {
            sb.append(I18n.t("result.phillips.no_data"));
        }

        return sb.toString().trim();
    }
}
