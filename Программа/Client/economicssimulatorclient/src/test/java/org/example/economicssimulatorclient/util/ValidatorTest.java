package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void testIsValidEmail() {
        assertTrue(Validator.isValidEmail("user@mail.com"));
        assertFalse(Validator.isValidEmail("not-an-email"));
        assertFalse(Validator.isValidEmail("user@com"));
        assertFalse(Validator.isValidEmail(null));
    }

    @Test
    void testIsStrongPassword() {
        assertTrue(Validator.isStrongPassword("Qwerty1!"));
        assertFalse(Validator.isStrongPassword("short1!"));
        assertFalse(Validator.isStrongPassword("nouppercase1!"));
        assertFalse(Validator.isStrongPassword("NOLOWERCASE1!"));
        assertFalse(Validator.isStrongPassword("NoSpecial123"));
        assertFalse(Validator.isStrongPassword(null));
    }
}
