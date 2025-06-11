package org.example.economicssimulatorclient.parser;

import org.example.economicssimulatorclient.util.I18n;
import org.json.JSONArray;
import org.json.JSONObject;

public class SolowGrowthResultParser implements ResultParser {

    @Override
    public String parse(String json) {
        StringBuilder sb = new StringBuilder();
        JSONObject root = new JSONObject(json);

        // 1. Основная траектория капитала и выпуска (выводим старт и конец)
        JSONObject trajectories = root.optJSONObject("trajectories");
        if (trajectories != null) {
            JSONArray capitalArr = trajectories.optJSONArray("capital");
            JSONArray outputArr = trajectories.optJSONArray("output");
            if (capitalArr != null && capitalArr.length() > 0 && outputArr != null && outputArr.length() > 0) {
                JSONObject capStart = capitalArr.getJSONObject(0);
                JSONObject capEnd = capitalArr.getJSONObject(capitalArr.length() - 1);
                JSONObject outStart = outputArr.getJSONObject(0);
                JSONObject outEnd = outputArr.getJSONObject(outputArr.length() - 1);

                sb.append(I18n.t("result.solow.capital_title")).append("\n");
                sb.append(String.format(I18n.t("result.solow.capital_start"), capStart.optDouble("time"), capStart.optDouble("capital")));
                sb.append(String.format(I18n.t("result.solow.capital_end"), capEnd.optDouble("time"), capEnd.optDouble("capital")));
                sb.append("\n");
                sb.append(I18n.t("result.solow.output_title")).append("\n");
                sb.append(String.format(I18n.t("result.solow.output_start"), outStart.optDouble("time"), outStart.optDouble("output")));
                sb.append(String.format(I18n.t("result.solow.output_end"), outEnd.optDouble("time"), outEnd.optDouble("output")));
                sb.append("\n");
            }
        }

        // 2. Фазовые изменения капитала
        JSONObject phase = root.optJSONObject("phase");
        if (phase != null) {
            JSONArray phaseArr = phase.optJSONArray("phase");
            if (phaseArr != null && phaseArr.length() > 0) {
                JSONObject phStart = phaseArr.getJSONObject(0);
                JSONObject phEnd = phaseArr.getJSONObject(phaseArr.length() - 1);

                sb.append(I18n.t("result.solow.phase_title")).append("\n");
                sb.append(String.format(I18n.t("result.solow.phase_start"),
                        phStart.optDouble("capital"), phStart.optDouble("capital_change")));
                sb.append(String.format(I18n.t("result.solow.phase_end"),
                        phEnd.optDouble("capital"), phEnd.optDouble("capital_change")));
                sb.append("\n");
            }
        }

        // 3. Сценарии для выпуска (baseline, high_savings)
        JSONObject statics = root.optJSONObject("statics");
        if (statics != null) {
            JSONObject scenarios = statics.optJSONObject("scenarios");
            if (scenarios != null) {
                // baseline
                JSONArray baseline = scenarios.optJSONArray("baseline");
                if (baseline != null && baseline.length() > 0) {
                    JSONObject start = baseline.getJSONObject(0);
                    JSONObject end = baseline.getJSONObject(baseline.length() - 1);
                    sb.append(I18n.t("result.solow.baseline_title")).append("\n");
                    sb.append(String.format(I18n.t("result.solow.scenario_start"), start.optDouble("output")));
                    sb.append(String.format(I18n.t("result.solow.scenario_end"), end.optDouble("output")));
                    sb.append("\n");
                }
                // high_savings (если есть)
                JSONArray highSavings = scenarios.optJSONArray("high_savings");
                if (highSavings != null && highSavings.length() > 0) {
                    JSONObject start = highSavings.getJSONObject(0);
                    JSONObject end = highSavings.getJSONObject(highSavings.length() - 1);
                    sb.append(I18n.t("result.solow.high_savings_title")).append("\n");
                    sb.append(String.format(I18n.t("result.solow.scenario_start"), start.optDouble("output")));
                    sb.append(String.format(I18n.t("result.solow.scenario_end"), end.optDouble("output")));
                    sb.append("\n");
                }
            }
        }

        if (sb.length() == 0) {
            sb.append(I18n.t("result.solow.no_data"));
        }

        return sb.toString().trim();
    }
}
