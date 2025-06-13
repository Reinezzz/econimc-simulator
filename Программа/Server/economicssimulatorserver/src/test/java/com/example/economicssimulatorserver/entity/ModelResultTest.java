package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ModelResultTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        EconomicModel model = EconomicModel.builder().id(1L).build();
        User user = User.builder().id(3L).build();
        LocalDateTime now = LocalDateTime.now();

        ModelResult result = ModelResult.builder()
                .id(2L)
                .model(model)
                .resultType("chart")
                .resultData("data")
                .calculatedAt(now)
                .user(user)
                .build();

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getModel()).isEqualTo(model);
        assertThat(result.getResultType()).isEqualTo("chart");
        assertThat(result.getResultData()).isEqualTo("data");
        assertThat(result.getCalculatedAt()).isEqualTo(now);
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void equalsHashCodeToString() {
        EconomicModel model = EconomicModel.builder().id(1L).build();
        User user = User.builder().id(3L).build();
        LocalDateTime now = LocalDateTime.now();
        ModelResult r1 = new ModelResult(2L, model, "t", "d", now, user);
        ModelResult r2 = new ModelResult(2L, model, "t", "d", now, user);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        assertThat(r1.toString()).contains("ModelResult");
    }
}
