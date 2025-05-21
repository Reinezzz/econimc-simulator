package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void testIsValidEmail() {
        assertTrue(Validator.isValidEmail("mail@example.com"));
        assertTrue(Validator.isValidEmail("some.user+1@sub.domain.com"));

        assertFalse(Validator.isValidEmail(null));
        assertFalse(Validator.isValidEmail(""));
        assertFalse(Validator.isValidEmail("invalidemail@"));
        assertFalse(Validator.isValidEmail("noatsign.com"));
    }

    @Test
    void testIsStrongPassword() {
        assertTrue(Validator.isStrongPassword("Strong1!q"));
        assertTrue(Validator.isStrongPassword("Qwe1rty!"));

        assertFalse(Validator.isStrongPassword(null));
        assertFalse(Validator.isStrongPassword("short"));
        assertFalse(Validator.isStrongPassword("alllowercase1!"));
        assertFalse(Validator.isStrongPassword("ALLUPPERCASE1!"));
        assertFalse(Validator.isStrongPassword("NoSpecial1"));
        assertFalse(Validator.isStrongPassword("Nodigit!"));
    }
}
