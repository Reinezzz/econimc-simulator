package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Парсер данных модели оценки опционов Блэка&#x2011;Шоулза.
 *
 * <p>Ожидаемая структура JSON:
 * <pre>
 * {
 *   "surface": {"surface": [[{"S": number, "C": number}, ...]]},
 *   "decay": {"decay": [{"T": number, "C": number}, ...]},
 *   "greeks": {"greeks": {"delta": [...], "gamma": [...], ...}}
 * }
 * </pre>
 */
public class BlackScholesResultParser implements ResultParser {

    /**
     * Формирует текстовые результаты расчёта модели Блэка‑Шоулза.
     *
     * @param json JSON, полученный от сервера
     * @return строка с описанием основных показателей
     */
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        JSONObject surfaceObj = root.optJSONObject("surface");
        if (surfaceObj != null) {
            JSONArray surfaceArr = surfaceObj.optJSONArray("surface");
            if (surfaceArr != null && !surfaceArr.isEmpty()) {
                JSONArray SRow = surfaceArr.getJSONArray(0);
                JSONArray CCol = surfaceArr.getJSONArray(surfaceArr.length() - 1);
                JSONObject minC = SRow.getJSONObject(0);
                JSONObject maxC = SRow.getJSONObject(SRow.length() - 1);
                double Smin = minC.optDouble("S", Double.NaN);
                double Smax = maxC.optDouble("S", Double.NaN);
                double Cmin = minC.optDouble("C", Double.NaN);
                double Cmax = maxC.optDouble("C", Double.NaN);

                sb.append(I18n.t("result.bs.surface_title")).append("\n");
                sb.append(String.format(I18n.t("result.bs.surface_range"), Smin, Smax, Cmin, Cmax));
            }
        }

        JSONObject decayObj = root.optJSONObject("decay");
        if (decayObj != null) {
            JSONArray decayArr = decayObj.optJSONArray("decay");
            if (decayArr != null && !decayArr.isEmpty()) {
                JSONObject first = decayArr.getJSONObject(0);
                JSONObject last = decayArr.getJSONObject(decayArr.length() - 1);
                double Tmax = first.optDouble("T", Double.NaN);
                double Tmin = last.optDouble("T", Double.NaN);
                double Cstart = first.optDouble("C", Double.NaN);
                double Cend = last.optDouble("C", Double.NaN);
                sb.append("\n").append(I18n.t("result.bs.decay_title")).append("\n");
                sb.append(String.format(I18n.t("result.bs.decay_desc"), Cstart, Cend, Tmax, Tmin));
            }
        }

        JSONObject greeksObj = root.optJSONObject("greeks");
        if (greeksObj != null) {
            JSONObject greeks = greeksObj.optJSONObject("greeks");
            if (greeks != null) {
                sb.append("\n").append(I18n.t("result.bs.greeks_title")).append("\n");
                appendGreek(sb, greeks, "delta", I18n.t("result.bs.delta"));
                appendGreek(sb, greeks, "gamma", I18n.t("result.bs.gamma"));
                appendGreek(sb, greeks, "vega", I18n.t("result.bs.vega"));
                appendGreek(sb, greeks, "theta", I18n.t("result.bs.theta"));
                appendGreek(sb, greeks, "rho", I18n.t("result.bs.rho"));
            }
        }

        if (sb.isEmpty()) {
            sb.append(I18n.t("result.bs.no_data"));
        }

        return sb.toString().trim();
    }

    /**
     * Вспомогательный метод для вывода диапазона значений конкретной греческой
     * буквы.
     *
     * @param sb    буфер, в который добавляется текст
     * @param greeks объект с массивами греческих букв
     * @param key   имя массива внутри объекта
     * @param label локализованное название буквы
     */
    private void appendGreek(StringBuilder sb, JSONObject greeks, String key, String label) {
        JSONArray arr = greeks.optJSONArray(key);
        if (arr != null && !arr.isEmpty()) {
            double minVal = arr.getJSONObject(0).optDouble("value", Double.NaN);
            double maxVal = arr.getJSONObject(arr.length() - 1).optDouble("value", Double.NaN);
            sb.append(String.format(I18n.t("result.bs.greek_range"), label, minVal, maxVal));
        }
    }
}
