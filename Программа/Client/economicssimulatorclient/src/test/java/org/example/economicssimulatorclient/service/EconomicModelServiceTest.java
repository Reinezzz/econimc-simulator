package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EconomicModelServiceTest {

    @Spy
    EconomicModelService service = Mockito.spy(new EconomicModelService());

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllModels_returnsList() throws Exception {
        EconomicModelDto[] models = {
                new EconomicModelDto(
                        1L,
                        "DemandSupply",
                        "Test model",
                        "Description",
                        List.of(
                                new ModelParameterDto(1L, 1L, "a", "double", "100", "A", "desc", 1)
                        ),
                        List.of(
                                new ModelResultDto(1L, 1L, "output", "{\"value\":42}", "2024-06-13T12:00:00")
                        ),
                        "2024-06-13T11:00:00",
                        "2024-06-13T12:00:00",
                        "Q = a - bP"
                )
        };
        doReturn(models).when(service)
                .get(any(), eq(""), eq(EconomicModelDto[].class), anyBoolean(), any());

        List<EconomicModelDto> result = service.getAllModels();
        assertEquals(1, result.size());
        EconomicModelDto model = result.get(0);
        assertEquals("Test model", model.name());
        assertEquals("DemandSupply", model.modelType());
        assertNotNull(model.parameters());
        assertEquals("A", model.parameters().get(0).displayName());
        assertNotNull(model.results());
        assertEquals("output", model.results().get(0).resultType());
    }

    @Test
    void getModelById_returnsModel() throws Exception {
        EconomicModelDto model = new EconomicModelDto(
                2L,
                "Elasticity",
                "Elasticity model",
                "Desc",
                List.of(),
                List.of(),
                null,
                null,
                null
        );
        doReturn(model).when(service)
                .get(any(), eq("2"), eq(EconomicModelDto.class), anyBoolean(), any());

        EconomicModelDto result = service.getModelById(2L);
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Elasticity", result.modelType());
    }

    @Test
    void getParametersByModelId_returnsParams() throws Exception {
        ModelParameterDto[] params = {
                new ModelParameterDto(10L, 2L, "b", "double", "3.5", "B", "desc", 2)
        };
        doReturn(params).when(service)
                .get(any(), eq("2/parameters"), eq(ModelParameterDto[].class), anyBoolean(), any());

        List<ModelParameterDto> result = service.getParametersByModelId(2L);
        assertEquals(1, result.size());
        assertEquals("b", result.get(0).paramName());
        assertEquals("B", result.get(0).displayName());
    }

    @Test
    void updateParameter_returnsUpdated() throws Exception {
        ModelParameterDto param = new ModelParameterDto(3L, 2L, "c", "int", "10", "C", "desc", 3);
        doReturn(param).when(service)
                .put(any(), eq("parameters/3"), eq(param), eq(ModelParameterDto.class), anyBoolean(), any());

        ModelParameterDto result = service.updateParameter(3L, param);
        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("C", result.displayName());
    }

    @Test
    void calculate_returnsResult() throws Exception {
        ModelResultDto modelResult = new ModelResultDto(
                5L,        // id
                1L,        // modelId
                "output",  // resultType
                "{\"val\":5}", // resultData
                "2024-06-13T15:00:00" // calculatedAt
        );
        List<ModelParameterDto> updatedParams = List.of(
                new ModelParameterDto(7L, 1L, "alpha", "double", "2.5", "Alpha", "desc", 1)
        );
        CalculationResponseDto resp = new CalculationResponseDto(modelResult, updatedParams);

        CalculationRequestDto req = new CalculationRequestDto(
                1L,                    // modelId
                "DemandSupply",        // modelType
                List.of(
                        new ModelParameterDto(10L, 1L, "a", "double", "100", "A", "desc", 1)
                )
        );

        doReturn(resp).when(service)
                .post(any(), eq("calculate"), eq(req), eq(CalculationResponseDto.class), anyBoolean(), any());

        CalculationResponseDto result = service.calculate(req);

        assertNotNull(result);
        assertNotNull(result.result());
        assertEquals("output", result.result().resultType());
        assertEquals("{\"val\":5}", result.result().resultData());
        assertNotNull(result.updatedParameters());
        assertFalse(result.updatedParameters().isEmpty());
        assertEquals("Alpha", result.updatedParameters().get(0).displayName());
    }

}
