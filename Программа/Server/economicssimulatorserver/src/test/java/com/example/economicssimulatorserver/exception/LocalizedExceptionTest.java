package com.example.economicssimulatorserver.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalizedExceptionTest {

    @Test
    void createAndReadFields() {
        LocalizedException ex = new LocalizedException("error.code", "arg1", 123);

        assertEquals("error.code", ex.getCode());
        assertArrayEquals(new Object[]{"arg1", 123}, ex.getArgs());
        assertEquals("error.code", ex.getMessage());
    }
}
