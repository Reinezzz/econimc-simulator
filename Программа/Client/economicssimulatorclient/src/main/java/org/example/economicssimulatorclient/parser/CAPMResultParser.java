package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Парсер результатов модели оценки активов (CAPM).
 *
 * <p>Ожидаемая структура JSON:
 * <pre>
 * {
 *   "sml": {"sml": [{"risk": number, "return": number}, ...]},
 *   "efficient_frontier": {"portfolios": [{"risk": number, "return": number}, ...]},
 *   "decomposition": {"decomposition": [{"label": string, "alpha": number, "beta": number}, ...]}
 * }
 * </pre>
 */
public class CAPMResultParser implements ResultParser {

    /**
     * Преобразует JSON ответа по модели CAPM в краткое текстовое описание.
     *
     * @param json данные от сервера
     * @return строка с результатами
     */
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        JSONObject sml = root.optJSONObject("sml");
        if (sml != null) {
            JSONArray smlArr = sml.optJSONArray("sml");
            if (smlArr != null && smlArr.length() >= 2) {
                JSONObject min = smlArr.getJSONObject(0);
                JSONObject max = smlArr.getJSONObject(smlArr.length() - 1);
                double riskMin = min.optDouble("risk", Double.NaN);
                double riskMax = max.optDouble("risk", Double.NaN);
                double retMin = min.optDouble("return", Double.NaN);
                double retMax = max.optDouble("return", Double.NaN);
                sb.append(I18n.t("result.capm.sml_title")).append("\n");
                sb.append(String.format(I18n.t("result.capm.range"),
                        retMin * 100, retMax * 100, riskMin, riskMax));
            }
        }

        JSONObject frontier = root.optJSONObject("efficient_frontier");
        if (frontier != null) {
            JSONArray portfolios = frontier.optJSONArray("portfolios");
            if (portfolios != null && !portfolios.isEmpty()) {
                JSONObject min = portfolios.getJSONObject(0);
                JSONObject max = portfolios.getJSONObject(portfolios.length() - 1);
                double riskMin = min.optDouble("risk", Double.NaN);
                double riskMax = max.optDouble("risk", Double.NaN);
                double retMin = min.optDouble("return", Double.NaN);
                double retMax = max.optDouble("return", Double.NaN);
                sb.append("\n").append(I18n.t("result.capm.frontier_title")).append("\n");
                sb.append(String.format(I18n.t("result.capm.range"),
                        retMin * 100, retMax * 100, riskMin, riskMax));
            }
        }
        JSONObject decomposition = root.optJSONObject("decomposition");
        if (decomposition != null) {
            JSONArray arr = decomposition.optJSONArray("decomposition");
            if (arr != null && !arr.isEmpty()) {
                sb.append("\n").append(I18n.t("result.capm.decomposition_title")).append("\n");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject comp = arr.getJSONObject(i);
                    String label = comp.optString("label", I18n.t("result.capm.no_name"));
                    double alpha = comp.optDouble("alpha", Double.NaN);
                    double beta = comp.optDouble("beta", Double.NaN);
                    sb.append(String.format(I18n.t("result.capm.decomposition_item"), label, alpha, beta));
                }
            }
        }

        if (sb.isEmpty()) {
            sb.append(I18n.t("result.capm.no_data"));
        }

        return sb.toString().trim();
    }
}
