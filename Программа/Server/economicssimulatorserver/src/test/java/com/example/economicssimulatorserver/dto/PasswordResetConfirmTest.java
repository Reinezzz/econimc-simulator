package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetConfirmTest {

    @Test
    void createAndReadFields() {
        PasswordResetConfirm confirm = new PasswordResetConfirm("email@ex.com", "123456", "newpass");
        assertEquals("email@ex.com", confirm.email());
        assertEquals("123456", confirm.code());
        assertEquals("newpass", confirm.newPassword());
    }
}
