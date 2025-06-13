package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EconomicModelTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        EconomicModel model = EconomicModel.builder()
                .id(10L)
                .modelType("macro")
                .name("IS-LM")
                .description("desc")
                .parameters(List.of())
                .results(List.of())
                .createdAt(now)
                .updatedAt(now)
                .formula("x+y=z")
                .build();

        assertThat(model.getId()).isEqualTo(10L);
        assertThat(model.getModelType()).isEqualTo("macro");
        assertThat(model.getName()).isEqualTo("IS-LM");
        assertThat(model.getDescription()).isEqualTo("desc");
        assertThat(model.getParameters()).isEmpty();
        assertThat(model.getResults()).isEmpty();
        assertThat(model.getCreatedAt()).isEqualTo(now);
        assertThat(model.getUpdatedAt()).isEqualTo(now);
        assertThat(model.getFormula()).isEqualTo("x+y=z");
    }


    @Test
    void equalsHashCodeToString() {
        EconomicModel m1 = new EconomicModel(1L, "t", "n", "d", List.of(), List.of(), LocalDateTime.now(), LocalDateTime.now(), "f");
        EconomicModel m2 = new EconomicModel(1L, "t", "n", "d", List.of(), List.of(), m1.getCreatedAt(), m1.getUpdatedAt(), "f");

        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
        assertThat(m1.toString()).contains("EconomicModel");
    }
}
