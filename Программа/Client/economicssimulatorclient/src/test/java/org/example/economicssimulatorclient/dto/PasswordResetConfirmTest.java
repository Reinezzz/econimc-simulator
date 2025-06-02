package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetConfirmTest {

    @Test
    void constructorAndGettersWork() {
        PasswordResetConfirm confirm = new PasswordResetConfirm("mail@mail.com", "code123", "newPass!");
        assertEquals("mail@mail.com", confirm.email());
        assertEquals("code123", confirm.code());
        assertEquals("newPass!", confirm.newPassword());
    }

    @Test
    void equalsAndHashCode() {
        PasswordResetConfirm c1 = new PasswordResetConfirm("e", "c", "p");
        PasswordResetConfirm c2 = new PasswordResetConfirm("e", "c", "p");
        PasswordResetConfirm c3 = new PasswordResetConfirm("other", "c", "p");
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        PasswordResetConfirm c = new PasswordResetConfirm("e", "k", "n");
        String s = c.toString();
        assertTrue(s.contains("e"));
        assertTrue(s.contains("k"));
        assertTrue(s.contains("n"));
    }
}
