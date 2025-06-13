package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ModelParameterRepositoryTest {

    @Autowired
    private ModelParameterRepository repository;
    @Autowired
    private EconomicModelRepository modelRepository;

    @Test
    void saveAndFindByModelId() {
        EconomicModel model = EconomicModel.builder()
                .modelType("macro")
                .name("Test")
                .description("desc")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        modelRepository.save(model);

        ModelParameter param = ModelParameter.builder()
                .model(model)
                .paramName("alpha")
                .paramType("double")
                .paramValue("0.5")
                .build();
        repository.save(param);

        List<ModelParameter> found = repository.findByModelId(model.getId());
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getParamName()).isEqualTo("alpha");
    }
}
