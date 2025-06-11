package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConsumerChoiceResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        JSONObject curvesObj = root.optJSONObject("indifference_curves");
        if (curvesObj != null) {
            JSONArray curves = curvesObj.optJSONArray("indifference_curves");
            if (curves != null && !curves.isEmpty()) {
                sb.append(I18n.t("result.consumer.curves_title")).append("\n");
                for (int i = 0; i < curves.length(); i++) {
                    JSONArray curve = curves.getJSONArray(i);
                    if (!curve.isEmpty()) {
                        JSONObject first = curve.getJSONObject(0);
                        JSONObject last = curve.getJSONObject(curve.length() - 1);
                        double qMin = first.optDouble("quantity", Double.NaN);
                        double qMax = last.optDouble("quantity", Double.NaN);
                        double pMin = first.optDouble("price", Double.NaN);
                        double pMax = last.optDouble("price", Double.NaN);
                        sb.append(String.format(I18n.t("result.consumer.curve"),
                                i + 1, qMin, qMax, pMin, pMax));
                    }
                }
            }
        }

        JSONArray budgetArr = null;
        if (curvesObj != null) {
            budgetArr = curvesObj.optJSONArray("budget");
        }
        if (budgetArr != null && !budgetArr.isEmpty()) {
            JSONObject first = budgetArr.getJSONObject(0);
            JSONObject last = budgetArr.getJSONObject(budgetArr.length() - 1);
            double qMin = first.optDouble("quantity", Double.NaN);
            double qMax = last.optDouble("quantity", Double.NaN);
            double pMin = first.optDouble("price", Double.NaN);
            double pMax = last.optDouble("price", Double.NaN);
            sb.append("\n").append(I18n.t("result.consumer.budget_title")).append("\n");
            sb.append(String.format(I18n.t("result.consumer.budget"), qMin, qMax, pMin, pMax));
        }

        JSONObject optMap = root.optJSONObject("optimum_map");
        if (optMap != null) {
            JSONObject opt = optMap.optJSONObject("optimum");
            if (opt != null) {
                double qOpt = opt.optDouble("x", Double.NaN);
                double pOpt = opt.optDouble("y", Double.NaN);
                sb.append("\n").append(I18n.t("result.consumer.optimum_title")).append("\n");
                sb.append(String.format(I18n.t("result.consumer.optimum"), qOpt, pOpt));
            }
        }

        JSONObject incomeSubObj = root.optJSONObject("income_substitution");
        if (incomeSubObj != null) {
            JSONArray subArr = incomeSubObj.optJSONArray("substitution");
            JSONArray incArr = incomeSubObj.optJSONArray("income");
            if (subArr != null && incArr != null && subArr.length() > 1 && incArr.length() > 1) {
                JSONObject base = subArr.getJSONObject(0);
                JSONObject sub = subArr.getJSONObject(1);
                JSONObject inc = incArr.getJSONObject(1);

                double qBase = base.optDouble("x", Double.NaN);
                double pBase = base.optDouble("y", Double.NaN);
                double qSub = sub.optDouble("x", Double.NaN);
                double pSub = sub.optDouble("y", Double.NaN);
                double qInc = inc.optDouble("x", Double.NaN);
                double pInc = inc.optDouble("y", Double.NaN);

                sb.append("\n").append(I18n.t("result.consumer.effect_title")).append("\n");
                sb.append(String.format(I18n.t("result.consumer.substitution"), qSub, pSub));
                sb.append(String.format(I18n.t("result.consumer.income"), qInc, pInc));
            }
        }

        if (sb.isEmpty()) {
            sb.append(I18n.t("result.consumer.no_data"));
        }

        return sb.toString().trim();
    }
}
