package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PendingRegistrationTest {

    @Test
    void createAndReadFields() {
        Instant now = Instant.now();
        PendingRegistration pending = new PendingRegistration("user", "mail", "hash", "123456", now);

        assertEquals("user", pending.username);
        assertEquals("mail", pending.email);
        assertEquals("hash", pending.passwordHash);
        assertEquals("123456", pending.code);
        assertEquals(now, pending.expiresAt);
    }
}
