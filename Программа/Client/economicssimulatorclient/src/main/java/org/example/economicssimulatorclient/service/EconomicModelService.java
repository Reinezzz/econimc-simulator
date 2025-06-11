package org.example.economicssimulatorclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class EconomicModelService extends MainService {

    private static final String BASE_API = "/api/models";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + BASE_API + "/");
    private static final ObjectMapper objectMapper = AppConfig.objectMapper;


    public List<EconomicModelDto> getAllModels() throws IOException, InterruptedException {
        String endpoint = "";
        EconomicModelDto[] arr = get(baseUri, endpoint, EconomicModelDto[].class, true, null);
        return Arrays.asList(arr);
    }

    public EconomicModelDto getModelById(Long id) throws IOException, InterruptedException {
        String endpoint = String.valueOf(id);
        return get(baseUri, endpoint, EconomicModelDto.class, true, null);
    }

    public List<ModelParameterDto> getParametersByModelId(Long modelId) throws IOException, InterruptedException {
        String endpoint = modelId + "/parameters";
        ModelParameterDto[] arr = get(baseUri, endpoint, ModelParameterDto[].class, true, null);
        return Arrays.asList(arr);
    }


    public ModelParameterDto updateParameter(Long paramId, ModelParameterDto dto) throws IOException, InterruptedException {
        String endpoint = "parameters/" + paramId;
        return put(baseUri, endpoint, dto, ModelParameterDto.class, true, null);
    }


    public CalculationResponseDto calculate(CalculationRequestDto request) throws IOException, InterruptedException {
        String endpoint = "calculate";
        return post(baseUri, endpoint, request, CalculationResponseDto.class, true, null);
    }
}
