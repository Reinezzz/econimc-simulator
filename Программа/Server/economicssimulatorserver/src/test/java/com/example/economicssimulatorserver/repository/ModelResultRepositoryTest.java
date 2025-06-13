package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelResult;
import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ModelResultRepositoryTest {

    @Autowired
    private ModelResultRepository repository;
    @Autowired
    private EconomicModelRepository modelRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByUserIdAndModelId() {
        User user = User.builder().username("vasya").email("vasya@mail.com").passwordHash("hash").build();
        userRepository.save(user);

        EconomicModel model = EconomicModel.builder().modelType("macro").name("IS-LM").description("desc")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        modelRepository.save(model);

        ModelResult result = ModelResult.builder()
                .model(model)
                .resultType("result")
                .resultData("data")
                .calculatedAt(LocalDateTime.now())
                .user(user)
                .build();
        repository.save(result);

        Optional<ModelResult> found = repository.findByUserIdAndModelId(user.getId(), model.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getResultData()).isEqualTo("data");
    }
}
