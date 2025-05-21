package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetConfirmTest {

    @Test
    void testConstructorAndGetters() {
        PasswordResetConfirm conf = new PasswordResetConfirm("mail@mail.com", "123456", "newPass");
        assertEquals("mail@mail.com", conf.email());
        assertEquals("123456", conf.code());
        assertEquals("newPass", conf.newPassword());
    }

    @Test
    void testNullsAndEmpty() {
        assertThrows(NullPointerException.class, () -> new PasswordResetConfirm(null, "c", "n"));
        assertThrows(NullPointerException.class, () -> new PasswordResetConfirm("e", null, "n"));
        assertThrows(NullPointerException.class, () -> new PasswordResetConfirm("e", "c", null));
    }
}
