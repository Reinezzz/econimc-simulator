package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.dto.ModelParameterDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterValidatorTest {

    @Test
    void isValid_handlesNullsAndUnknownTypes() {
        assertThat(ParameterValidator.isValid(null)).isFalse();
        ModelParameterDto dto = new ModelParameterDto(1L, 1L, "a", "unknown", "123", "disp", "desc", 1);
        assertThat(ParameterValidator.isValid(dto)).isFalse();
    }

    @Test
    void isValid_handlesNumbers() {
        var intParam = new ModelParameterDto(1L, 1L, "p", "int", "12", "disp", "desc", 1);
        assertThat(ParameterValidator.isValid(intParam)).isTrue();

        var doubleParam = new ModelParameterDto(1L, 1L, "p", "double", "12.5", "disp", "desc", 1);
        assertThat(ParameterValidator.isValid(doubleParam)).isTrue();

        var badInt = new ModelParameterDto(1L, 1L, "p", "int", "notInt", "disp", "desc", 1);
        assertThat(ParameterValidator.isValid(badInt)).isFalse();
    }

    @Test
    void isValid_handlesBooleans() {
        var trueParam = new ModelParameterDto(1L, 1L, "p", "boolean", "true", "disp", "desc", 1);
        var falseParam = new ModelParameterDto(1L, 1L, "p", "boolean", "FALSE", "disp", "desc", 1);
        var badParam = new ModelParameterDto(1L, 1L, "p", "boolean", "yes", "disp", "desc", 1);
        assertThat(ParameterValidator.isValid(trueParam)).isTrue();
        assertThat(ParameterValidator.isValid(falseParam)).isTrue();
        assertThat(ParameterValidator.isValid(badParam)).isFalse();
    }

    @Test
    void isValid_handlesJsonType() {
        var obj = new ModelParameterDto(1L, 1L, "p", "json", "{\"a\":1}", "disp", "desc", 1);
        var arr = new ModelParameterDto(1L, 1L, "p", "json", "[1,2]", "disp", "desc", 1);
        var bad = new ModelParameterDto(1L, 1L, "p", "json", "notjson", "disp", "desc", 1);
        assertThat(ParameterValidator.isValid(obj)).isTrue();
        assertThat(ParameterValidator.isValid(arr)).isTrue();
        assertThat(ParameterValidator.isValid(bad)).isFalse();
    }

    @Test
    void notEmpty_worksAsExpected() {
        assertThat(ParameterValidator.notEmpty("abc")).isTrue();
        assertThat(ParameterValidator.notEmpty("")).isFalse();
        assertThat(ParameterValidator.notEmpty("   ")).isFalse();
        assertThat(ParameterValidator.notEmpty(null)).isFalse();
    }
}
