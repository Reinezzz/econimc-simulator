package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Парсер результатов макроэкономической модели IS&#x2011;LM.
 *
 * <p>Ожидаемая структура JSON:
 * <pre>
 * {
 *   "is_lm": {
 *     "equilibrium": {"rate": number, "income": number},
 *     "IS": [...],
 *     "LM": [...]
 *   },
 *   "timeseries": {"policy": [...]},
 *   "surface": {"slices": [[...]]}
 * }
 * </pre>
 */
public class ISLMResultParser implements ResultParser {

    /**
     * Разбирает JSON для модели IS‑LM и возвращает текстовый отчёт.
     *
     * @param json входные данные от сервера
     * @return сформированное описание
     */
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        JSONObject islm = root.optJSONObject("is_lm");
        if (islm != null) {
            JSONObject equilibrium = islm.optJSONObject("equilibrium");
            if (equilibrium != null) {
                sb.append(I18n.t("result.islm.equilibrium_title")).append("\n");
                sb.append(String.format(I18n.t("result.islm.rate"), equilibrium.optDouble("rate")));
                sb.append(String.format(I18n.t("result.islm.income"), equilibrium.optDouble("income")));
                sb.append("\n");
            }

            JSONArray isCurve = islm.optJSONArray("IS");
            JSONArray lmCurve = islm.optJSONArray("LM");
            if (isCurve != null && !isCurve.isEmpty() && lmCurve != null && !lmCurve.isEmpty()) {
                JSONObject isStart = isCurve.getJSONObject(0);
                JSONObject isEnd = isCurve.getJSONObject(isCurve.length() - 1);
                JSONObject lmStart = lmCurve.getJSONObject(0);
                JSONObject lmEnd = lmCurve.getJSONObject(lmCurve.length() - 1);

                sb.append(I18n.t("result.islm.is_curve_title")).append("\n");
                sb.append(String.format(I18n.t("result.islm.curve_start"), isStart.optDouble("rate"), isStart.optDouble("income")));
                sb.append(String.format(I18n.t("result.islm.curve_end"), isEnd.optDouble("rate"), isEnd.optDouble("income")));
                sb.append("\n");

                sb.append(I18n.t("result.islm.lm_curve_title")).append("\n");
                sb.append(String.format(I18n.t("result.islm.curve_start"), lmStart.optDouble("rate"), lmStart.optDouble("income")));
                sb.append(String.format(I18n.t("result.islm.curve_end"), lmEnd.optDouble("rate"), lmEnd.optDouble("income")));
                sb.append("\n");
            }
        }

        JSONObject timeseries = root.optJSONObject("timeseries");
        if (timeseries != null) {
            JSONArray policy = timeseries.optJSONArray("policy");
            if (policy != null && !policy.isEmpty()) {
                double startIncome = policy.getJSONObject(0).optDouble("income");
                double endIncome = policy.getJSONObject(policy.length() - 1).optDouble("income");
                sb.append(I18n.t("result.islm.dynamics_title")).append("\n");
                sb.append(String.format(I18n.t("result.islm.dynamics_start"), startIncome));
                sb.append(String.format(I18n.t("result.islm.dynamics_end"), endIncome));
                sb.append("\n");
            }
        }

        JSONObject surface = root.optJSONObject("surface");
        if (surface != null) {
            JSONArray slices = surface.optJSONArray("slices");
            if (slices != null && !slices.isEmpty()) {
                sb.append(I18n.t("result.islm.surface_title")).append("\n");
                for (int i = 0; i < slices.length(); i++) {
                    JSONArray slice = slices.getJSONArray(i);
                    if (!slice.isEmpty()) {
                        JSONObject start = slice.getJSONObject(0);
                        JSONObject end = slice.getJSONObject(slice.length() - 1);
                        sb.append(String.format(I18n.t("result.islm.slice"),
                                i + 1,
                                start.optDouble("rate"), start.optDouble("income"),
                                end.optDouble("rate"), end.optDouble("income")));
                    }
                }
                sb.append("\n");
            }
        }

        if (sb.isEmpty()) {
            sb.append(I18n.t("result.islm.no_data"));
        }

        return sb.toString().trim();
    }
}
