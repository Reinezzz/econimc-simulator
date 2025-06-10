package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class PhillipsCurveResultParser implements ResultParser {

    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // Краткое описание результата
        sb.append("Кривая Филлипса:\n");

        // 1. Диапазоны инфляции и безработицы
        JSONObject scatter = root.optJSONObject("scatter");
        if (scatter != null) {
            JSONArray points = scatter.optJSONArray("points");
            if (points != null && points.length() > 0) {
                JSONObject p0 = points.getJSONObject(0);
                JSONObject p1 = points.getJSONObject(points.length() - 1);

                double infMin = p0.optDouble("inflation");
                double infMax = p1.optDouble("inflation");
                double unMin = p0.optDouble("unemployment");
                double unMax = p1.optDouble("unemployment");

                sb.append(String.format(
                        "  • Диапазон инфляции: %.2f%% ... %.2f%%\n", infMin, infMax
                ));
                sb.append(String.format(
                        "  • Диапазон безработицы: %.2f%% ... %.2f%%\n", unMin, unMax
                ));
            }
        }

        // 2. Временные ряды: старт и конец периода
        JSONObject timeseries = root.optJSONObject("timeseries");
        if (timeseries != null) {
            JSONArray infArr = timeseries.optJSONArray("inflation");
            JSONArray unArr = timeseries.optJSONArray("unemployment");
            if (infArr != null && infArr.length() > 0 && unArr != null && unArr.length() > 0) {
                JSONObject infStart = infArr.getJSONObject(0);
                JSONObject infEnd = infArr.getJSONObject(infArr.length() - 1);
                JSONObject unStart = unArr.getJSONObject(0);
                JSONObject unEnd = unArr.getJSONObject(unArr.length() - 1);

                sb.append("\nДинамика по годам:\n");
                sb.append(String.format("  • В начале: инфляция = %.2f%%, безработица = %.2f%%\n",
                        infStart.optDouble("inflation"), unStart.optDouble("unemployment")));
                sb.append(String.format("  • В конце: инфляция = %.2f%%, безработица = %.2f%%\n",
                        infEnd.optDouble("inflation"), unEnd.optDouble("unemployment")));
            }
        }

        // 3. Краткосрочная и долгосрочная кривая
        JSONObject loops = root.optJSONObject("loops");
        if (loops != null) {
            JSONArray shortRun = loops.optJSONArray("short_run");
            JSONArray longRun = loops.optJSONArray("long_run");
            if (shortRun != null && shortRun.length() > 0) {
                JSONObject p0 = shortRun.getJSONObject(0);
                JSONObject p1 = shortRun.getJSONObject(shortRun.length() - 1);
                sb.append("\nКраткосрочная кривая:\n");
                sb.append(String.format(
                        "  • От инфляции %.2f%% до %.2f%% при безработице %.2f%% ... %.2f%%\n",
                        p0.optDouble("inflation"), p1.optDouble("inflation"),
                        p0.optDouble("unemployment"), p1.optDouble("unemployment")
                ));
            }
            if (longRun != null && longRun.length() > 0) {
                double lrInfl = longRun.getJSONObject(0).optDouble("inflation");
                JSONObject p0 = longRun.getJSONObject(0);
                JSONObject p1 = longRun.getJSONObject(longRun.length() - 1);
                sb.append("Долгосрочная кривая:\n");
                sb.append(String.format(
                        "  • Инфляция стабилизируется на %.2f%% при росте безработицы с %.2f%% до %.2f%%\n",
                        lrInfl, p0.optDouble("unemployment"), p1.optDouble("unemployment")
                ));
            }
        }

        if (sb.length() == 0) {
            sb.append("Нет данных для отображения (Phillips Curve).");
        }

        return sb.toString().trim();
    }
}
