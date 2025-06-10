package org.example.economicssimulatorclient.parser;

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

                sb.append("Траектория роста капитала:\n");
                sb.append(String.format("  • В начальный момент (t=%.0f): капитал = %.2f\n", capStart.optDouble("time"), capStart.optDouble("capital")));
                sb.append(String.format("  • В конце периода (t=%.0f): капитал = %.2f\n", capEnd.optDouble("time"), capEnd.optDouble("capital")));
                sb.append("\n");
                sb.append("Траектория роста выпуска:\n");
                sb.append(String.format("  • В начальный момент (t=%.0f): выпуск = %.2f\n", outStart.optDouble("time"), outStart.optDouble("output")));
                sb.append(String.format("  • В конце периода (t=%.0f): выпуск = %.2f\n", outEnd.optDouble("time"), outEnd.optDouble("output")));
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

                sb.append("Фазовая динамика капитала:\n");
                sb.append(String.format("  • В начале: капитал = %.2f, изменение = %.2f\n",
                        phStart.optDouble("capital"), phStart.optDouble("capital_change")));
                sb.append(String.format("  • В конце: капитал = %.2f, изменение = %.2f\n",
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
                    sb.append("Базовый сценарий выпуска:\n");
                    sb.append(String.format("  • В начальный момент: выпуск = %.2f\n", start.optDouble("output")));
                    sb.append(String.format("  • В конце периода: выпуск = %.2f\n", end.optDouble("output")));
                    sb.append("\n");
                }
                // high_savings (если есть)
                JSONArray highSavings = scenarios.optJSONArray("high_savings");
                if (highSavings != null && highSavings.length() > 0) {
                    JSONObject start = highSavings.getJSONObject(0);
                    JSONObject end = highSavings.getJSONObject(highSavings.length() - 1);
                    sb.append("Сценарий с высоким сбережением:\n");
                    sb.append(String.format("  • В начальный момент: выпуск = %.2f\n", start.optDouble("output")));
                    sb.append(String.format("  • В конце периода: выпуск = %.2f\n", end.optDouble("output")));
                    sb.append("\n");
                }
            }
        }

        if (sb.length() == 0) {
            sb.append("Нет данных для отображения (Solow Growth).");
        }

        return sb.toString().trim();
    }
}
