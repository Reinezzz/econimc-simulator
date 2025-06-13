package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserModelParameterTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        User user = User.builder().id(1L).build();
        EconomicModel model = EconomicModel.builder().id(2L).build();
        ModelParameter param = ModelParameter.builder().id(3L).build();

        UserModelParameter ump = UserModelParameter.builder()
                .id(10L)
                .user(user)
                .model(model)
                .parameter(param)
                .value("abc")
                .build();

        assertThat(ump.getId()).isEqualTo(10L);
        assertThat(ump.getUser()).isEqualTo(user);
        assertThat(ump.getModel()).isEqualTo(model);
        assertThat(ump.getParameter()).isEqualTo(param);
        assertThat(ump.getValue()).isEqualTo("abc");
    }

    @Test
    void equalsHashCodeToString() {
        User user = User.builder().id(1L).build();
        EconomicModel model = EconomicModel.builder().id(2L).build();
        ModelParameter param = ModelParameter.builder().id(3L).build();

        UserModelParameter ump1 = new UserModelParameter(10L, user, model, param, "abc");
        UserModelParameter ump2 = new UserModelParameter(10L, user, model, param, "abc");

        assertThat(ump1).isEqualTo(ump2);
        assertThat(ump1.hashCode()).isEqualTo(ump2.hashCode());
        assertThat(ump1.toString()).contains("UserModelParameter");
    }
}
