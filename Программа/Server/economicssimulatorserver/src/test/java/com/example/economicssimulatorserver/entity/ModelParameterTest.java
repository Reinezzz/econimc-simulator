package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelParameterTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        EconomicModel model = EconomicModel.builder().id(1L).build();
        ModelParameter param = ModelParameter.builder()
                .id(2L)
                .model(model)
                .paramName("a")
                .paramType("int")
                .paramValue("5")
                .displayName("A")
                .description("desc")
                .customOrder(1)
                .build();

        assertThat(param.getId()).isEqualTo(2L);
        assertThat(param.getModel()).isEqualTo(model);
        assertThat(param.getParamName()).isEqualTo("a");
        assertThat(param.getParamType()).isEqualTo("int");
        assertThat(param.getParamValue()).isEqualTo("5");
        assertThat(param.getDisplayName()).isEqualTo("A");
        assertThat(param.getDescription()).isEqualTo("desc");
        assertThat(param.getCustomOrder()).isEqualTo(1);
    }

    @Test
    void equalsHashCodeToString() {
        EconomicModel model = EconomicModel.builder().id(1L).build();
        ModelParameter p1 = new ModelParameter(2L, model, "a", "b", "c", "d", "e", 3);
        ModelParameter p2 = new ModelParameter(2L, model, "a", "b", "c", "d", "e", 3);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1.toString()).contains("ModelParameter");
    }
}
