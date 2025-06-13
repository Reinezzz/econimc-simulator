package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.exception.LocalizedException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ParameterTypeConverterTest {

    @Test
    void fromString_shouldConvertToInt() {
        Object res = ParameterTypeConverter.fromString("42", "int");
        assertThat(res).isInstanceOf(Integer.class).isEqualTo(42);
    }

    @Test
    void fromString_shouldConvertToDouble() {
        Object res = ParameterTypeConverter.fromString("3.14", "double");
        assertThat(res).isInstanceOf(Double.class).isEqualTo(3.14);
    }

    @Test
    void fromString_shouldConvertToBoolean() {
        Object res = ParameterTypeConverter.fromString("true", "boolean");
        assertThat(res).isInstanceOf(Boolean.class).isEqualTo(true);
    }

    @Test
    void fromString_shouldConvertToJsonNode() {
        Object res = ParameterTypeConverter.fromString("{\"k\":1}", "json");
        assertThat(res).isInstanceOf(JsonNode.class);
        assertThat(((JsonNode)res).get("k").asInt()).isEqualTo(1);
    }

    @Test
    void fromString_shouldReturnOriginal_whenUnknownType() {
        Object res = ParameterTypeConverter.fromString("abc", "string");
        assertThat(res).isEqualTo("abc");
    }

    @Test
    void fromString_shouldReturnNull_whenNullInput() {
        Object res = ParameterTypeConverter.fromString(null, "int");
        assertThat(res).isNull();
    }

    @Test
    void fromString_shouldThrow_onInvalidJson() {
        assertThatThrownBy(() -> ParameterTypeConverter.fromString("{not_json}", "json"))
                .isInstanceOf(LocalizedException.class)
                .hasMessageContaining("error.json_parse");
    }

    @Test
    void toString_shouldSerializeJson() {
        Map<String, Object> map = Map.of("x", 1);
        String json = ParameterTypeConverter.toString(map, "json");
        assertThat(json).contains("\"x\":1");
    }

    @Test
    void toString_shouldReturnToString_whenNotJson() {
        String res = ParameterTypeConverter.toString(42, "int");
        assertThat(res).isEqualTo("42");
    }

    @Test
    void toString_shouldReturnNull_whenNullInput() {
        assertThat(ParameterTypeConverter.toString(null, "json")).isNull();
    }
}
