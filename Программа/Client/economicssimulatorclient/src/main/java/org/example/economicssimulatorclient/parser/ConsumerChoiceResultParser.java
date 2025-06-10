package org.example.economicssimulatorclient.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConsumerChoiceResultParser implements ResultParser {
    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // Кривые безразличия
        JSONObject curvesObj = root.optJSONObject("indifference_curves");
        if (curvesObj != null) {
            JSONArray curves = curvesObj.optJSONArray("indifference_curves");
            if (curves != null && curves.length() > 0) {
                sb.append("Кривые безразличия (utility):\n");
                for (int i = 0; i < curves.length(); i++) {
                    JSONArray curve = curves.getJSONArray(i);
                    if (curve.length() > 0) {
                        JSONObject first = curve.getJSONObject(0);
                        JSONObject last = curve.getJSONObject(curve.length() - 1);
                        double qMin = first.optDouble("quantity", Double.NaN);
                        double qMax = last.optDouble("quantity", Double.NaN);
                        double pMin = first.optDouble("price", Double.NaN);
                        double pMax = last.optDouble("price", Double.NaN);
                        sb.append(String.format("  • Кривая %d: кол-во от %.2f до %.2f, цена от %.2f до %.2f\n",
                                i + 1, qMin, qMax, pMin, pMax));
                    }
                }
            }
        }

        // Бюджетное ограничение
        JSONArray budgetArr = null;
        if (curvesObj != null) {
            budgetArr = curvesObj.optJSONArray("budget");
        }
        if (budgetArr != null && budgetArr.length() > 0) {
            JSONObject first = budgetArr.getJSONObject(0);
            JSONObject last = budgetArr.getJSONObject(budgetArr.length() - 1);
            double qMin = first.optDouble("quantity", Double.NaN);
            double qMax = last.optDouble("quantity", Double.NaN);
            double pMin = first.optDouble("price", Double.NaN);
            double pMax = last.optDouble("price", Double.NaN);
            sb.append("\nБюджетное ограничение:\n");
            sb.append(String.format("  • Кол-во от %.2f до %.2f, цена от %.2f до %.2f\n", qMin, qMax, pMin, pMax));
        }

        // Оптимум на карте
        JSONObject optMap = root.optJSONObject("optimum_map");
        if (optMap != null) {
            JSONObject opt = optMap.optJSONObject("optimum");
            if (opt != null) {
                double qOpt = opt.optDouble("x", Double.NaN);
                double pOpt = opt.optDouble("y", Double.NaN);
                sb.append("\nТочка максимума полезности (оптимум):\n");
                sb.append(String.format("  • Оптимальное количество: %.2f, цена: %.2f\n", qOpt, pOpt));
            }
        }

        // Эффект дохода и замещения
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

                sb.append("\nРазложение эффекта изменения цены:\n");
                sb.append(String.format("  • Эффект замещения: оптимум изменился до (%.2f; %.2f)\n", qSub, pSub));
                sb.append(String.format("  • Эффект дохода: оптимум изменился до (%.2f; %.2f)\n", qInc, pInc));
            }
        }

        if (sb.length() == 0) {
            sb.append("Нет данных для отображения (Consumer Choice).");
        }

        return sb.toString().trim();
    }
}
