package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.EconomicModelDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EconomicModelServiceTest {

    @Mock EconomicModelRepository repo;
    @Mock ModelParameterService paramService;
    @InjectMocks EconomicModelService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllModels_returnsDtos() {
        EconomicModel model = new EconomicModel();
        model.setId(1L);
        model.setModelType("TYPE");
        model.setName("Model");
        model.setDescription("desc");
        when(repo.findAll()).thenReturn(List.of(model));
        when(paramService.getParametersByModelId(1L, 100L)).thenReturn(List.of(
                new ModelParameterDto(10L, 1L, "x", "NUMBER", "7", "X", "desc", 0)
        ));

        List<EconomicModelDto> list = service.getAllModels(100L);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).name()).isEqualTo("Model");
    }

    @Test
    void getModelById_notFound_throws() {
        when(repo.findById(17L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getModelById(17L, 1L)).isInstanceOf(LocalizedException.class);
    }
}
