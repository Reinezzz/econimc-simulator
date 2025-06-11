package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.example.economicssimulatorserver.util.ParameterTypeConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BlackScholesSolver implements EconomicModelSolver {

    @Override
    public CalculationResponseDto solve(CalculationRequestDto request) {
        Map<String, String> paramMap = request.parameters().stream()
                .collect(Collectors.toMap(ModelParameterDto::paramName, ModelParameterDto::paramValue));

        double S      = (Double) ParameterTypeConverter.fromString(paramMap.get("S"), "double");
        double K      = (Double) ParameterTypeConverter.fromString(paramMap.get("K"), "double");
        double T      = (Double) ParameterTypeConverter.fromString(paramMap.get("T"), "double");
        double r      = (Double) ParameterTypeConverter.fromString(paramMap.get("r"), "double");
        double sigma  = (Double) ParameterTypeConverter.fromString(paramMap.get("sigma"), "double");
        String option = paramMap.getOrDefault("option_type", "call");

        int timeSlices  = 5;
        int pricePoints = 40;

        double S_min = S * 0.5;
        double S_max = S * 1.5;
        double dS    = (S_max - S_min) / (pricePoints - 1);

        List<List<Map<String, Number>>> surfaceList = new ArrayList<>(timeSlices);
        for (int i = 1; i <= timeSlices; i++) {
            double t_i = T * i / (double) timeSlices;
            List<Map<String, Number>> slicePoints = new ArrayList<>(pricePoints);
            for (int j = 0; j < pricePoints; j++) {
                double S_j = S_min + dS * j;
                double C_j = blackScholesPrice(S_j, K, t_i, r, sigma, option);
                slicePoints.add(Map.of(
                        "S", S_j,
                        "C", C_j
                ));
            }
            surfaceList.add(slicePoints);
        }

        Map<String, Object> surfaceData = new LinkedHashMap<>();
        surfaceData.put("surface", surfaceList);

        int decayPoints = 40;
        List<Map<String, Number>> decayList = new ArrayList<>(decayPoints);
        for (int i = 0; i < decayPoints; i++) {
            double t_i = T * (1.0 - (double) i / (decayPoints - 1));
            if (t_i < 1e-6) t_i = 1e-6;
            double C_i = blackScholesPrice(S, K, t_i, r, sigma, option);
            decayList.add(Map.of(
                    "T", t_i,
                    "C", C_i
            ));
        }
        Map<String, Object> decayData = new LinkedHashMap<>();
        decayData.put("decay", decayList);

        Map<String, List<Map<String, Number>>> greeksMap = new LinkedHashMap<>();
        List<String> greekNames = List.of("delta", "gamma", "vega", "theta", "rho");
        for (String g : greekNames) {
            greeksMap.put(g, new ArrayList<>(pricePoints));
        }
        for (int j = 0; j < pricePoints; j++) {
            double S_j = S_min + dS * j;
            double d1 = (Math.log(S_j / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
            double d2 = d1 - sigma * Math.sqrt(T);
            double pdfD1 = pdf(d1);

            double deltaVal = option.equalsIgnoreCase("put")
                    ? cdf(d1) - 1
                    : cdf(d1);
            double gammaVal = pdfD1 / (S_j * sigma * Math.sqrt(T));
            double vegaVal = S_j * pdfD1 * Math.sqrt(T) / 100.0;
            double thetaVal = - (S_j * pdfD1 * sigma) / (2 * Math.sqrt(T));
            if (option.equalsIgnoreCase("call")) {
                thetaVal += - r * K * Math.exp(-r * T) * cdf(d2);
            } else {
                thetaVal += r * K * Math.exp(-r * T) * cdf(-d2);
            }
            thetaVal /= 365.0;
            double rhoVal = option.equalsIgnoreCase("put")
                    ? - K * T * Math.exp(-r * T) * cdf(-d2) / 100.0
                    : K * T * Math.exp(-r * T) * cdf(d2) / 100.0;

            greeksMap.get("delta").add(Map.of("S", S_j, "value", deltaVal));
            greeksMap.get("gamma").add(Map.of("S", S_j, "value", gammaVal));
            greeksMap.get("vega").add(Map.of("S", S_j, "value", vegaVal));
            greeksMap.get("theta").add(Map.of("S", S_j, "value", thetaVal));
            greeksMap.get("rho").add(Map.of("S", S_j, "value", rhoVal));
        }
        Map<String, Object> greeksData = new LinkedHashMap<>();
        greeksData.put("greeks", greeksMap);

        Map<String, Object> allCharts = new LinkedHashMap<>();
        allCharts.put("surface", surfaceData);
        allCharts.put("decay",   decayData);
        allCharts.put("greeks",  greeksData);

        ModelResultDto result = new ModelResultDto(
                null,
                request.modelId(),
                "chart",
                ParameterTypeConverter.toString(allCharts, "json"),
                null
        );
        return new CalculationResponseDto(result, request.parameters());
    }

    @Override
    public String getModelType() {
        return "BlackScholes";
    }

    private static double blackScholesPrice(double S, double K, double T, double r, double sigma, String option) {
        double d1 = (Math.log(S / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        if ("put".equalsIgnoreCase(option)) {
            return K * Math.exp(-r * T) * cdf(-d2) - S * cdf(-d1);
        } else {
            return S * cdf(d1) - K * Math.exp(-r * T) * cdf(d2);
        }
    }

    private static double pdf(double x) {
        return Math.exp(-0.5 * x * x) / Math.sqrt(2.0 * Math.PI);
    }

    private static double cdf(double x) {
        return 0.5 * (1.0 + erf(x / Math.sqrt(2.0)));
    }

    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
        double ans = 1 - t * Math.exp(-z*z - 1.26551223 +
                t * (1.00002368 +
                        t * (0.37409196 +
                                t * (0.09678418 +
                                        t * (-0.18628806 +
                                                t * (0.27886807 +
                                                        t * (-1.13520398 +
                                                                t * (1.48851587 +
                                                                        t * (-0.82215223 +
                                                                                t * 0.17087277)))))))));
        return (z >= 0) ? ans : -ans;
    }
}
