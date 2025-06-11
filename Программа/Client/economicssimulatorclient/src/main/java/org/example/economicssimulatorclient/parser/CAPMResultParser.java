package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

public class CAPMResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // SML: Security Market Line
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

        // Efficient Frontier
        JSONObject frontier = root.optJSONObject("efficient_frontier");
        if (frontier != null) {
            JSONArray portfolios = frontier.optJSONArray("portfolios");
            if (portfolios != null && portfolios.length() > 0) {
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

        // Decomposition: alpha, beta, labels
        JSONObject decomposition = root.optJSONObject("decomposition");
        if (decomposition != null) {
            JSONArray arr = decomposition.optJSONArray("decomposition");
            if (arr != null && arr.length() > 0) {
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

        if (sb.length() == 0) {
            sb.append(I18n.t("result.capm.no_data"));
        }

        return sb.toString().trim();
    }
}
