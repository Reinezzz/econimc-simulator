package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.entity.*;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelParameterServiceTest {

    @Mock ModelParameterRepository modelParameterRepo;
    @Mock EconomicModelRepository economicModelRepo;
    @Mock UserModelParameterRepository userModelParameterRepo;
    @Mock UserRepository userRepo;
    @InjectMocks ModelParameterService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getParametersByModelId_createsUserParamIfMissing() {
        long modelId = 1L, userId = 2L, paramId = 5L;
        ModelParameter param = new ModelParameter();
        param.setId(paramId);
        param.setParamName("alpha");
        param.setParamType("NUMBER");
        param.setParamValue("7");
        EconomicModel model = new EconomicModel();
        model.setId(modelId);
        param.setModel(model);

        when(modelParameterRepo.findByModelId(modelId)).thenReturn(List.of(param));
        when(userModelParameterRepo.findByUserIdAndModelId(userId, modelId)).thenReturn(List.of());
        when(userModelParameterRepo.findByUserIdAndParameterId(userId, paramId)).thenReturn(Optional.empty());
        User user = new User();
        user.setId(userId);
        when(userRepo.getReferenceById(userId)).thenReturn(user);
        when(economicModelRepo.getReferenceById(modelId)).thenReturn(model);
        when(modelParameterRepo.getReferenceById(paramId)).thenReturn(param);

        List<ModelParameterDto> dtos = service.getParametersByModelId(modelId, userId);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).paramName()).isEqualTo("alpha");
        verify(userModelParameterRepo).saveAll(anyList());
    }

    @Test
    void updateParameter_existing_updatesValue() {
        long paramId = 3L, userId = 2L;
        ModelParameter param = new ModelParameter();
        param.setId(paramId);
        param.setParamName("beta");
        param.setParamType("NUMBER");
        param.setParamValue("9");
        EconomicModel model = new EconomicModel();
        model.setId(1L);
        param.setModel(model);

        UserModelParameter ump = new UserModelParameter();
        ump.setParameter(param);
        ump.setValue("9");
        ump.setUser(new User());
        ump.setModel(model);

        when(userModelParameterRepo.findByUserIdAndParameterId(userId, paramId)).thenReturn(Optional.of(ump));
        when(modelParameterRepo.findById(paramId)).thenReturn(Optional.of(param));

        ModelParameterDto dto = new ModelParameterDto(paramId, 1L, "beta", "NUMBER", "15", "Beta", "desc", 0);
        ModelParameterDto updated = service.updateParameter(paramId, dto, userId);

        assertThat(updated.paramValue()).isEqualTo("15");
        verify(userModelParameterRepo).save(any());
    }

    @Test
    void updateParameter_nonExisting_createsNew() {
        long paramId = 7L, userId = 1L;
        ModelParameter param = new ModelParameter();
        param.setId(paramId);
        param.setParamName("c");
        param.setParamType("NUMBER");
        param.setParamValue("5");
        EconomicModel model = new EconomicModel();
        model.setId(4L);
        param.setModel(model);

        when(userModelParameterRepo.findByUserIdAndParameterId(userId, paramId)).thenReturn(Optional.empty());
        when(userRepo.getReferenceById(userId)).thenReturn(new User());
        when(economicModelRepo.getReferenceById(4L)).thenReturn(model);
        when(modelParameterRepo.getReferenceById(paramId)).thenReturn(param);
        when(modelParameterRepo.findById(paramId)).thenReturn(Optional.of(param));

        ModelParameterDto dto = new ModelParameterDto(paramId, 4L, "c", "NUMBER", "10", "C", "desc", 0);
        ModelParameterDto result = service.updateParameter(paramId, dto, userId);

        assertThat(result.paramValue()).isEqualTo("10");
        verify(userModelParameterRepo).save(any());
    }
}
