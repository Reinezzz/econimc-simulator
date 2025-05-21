package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Полноценно тестировать SceneManager можно только в интеграционных/FX тестах, тут минимальная smoke/proxy проверка

class SceneManagerTest {

    @Test
    void testSingletonClass() {
        // Проверим, что нельзя создать экземпляр
        assertThrows(UnsupportedOperationException.class, () -> {
            // Через рефлексию только можно создать, обычный new не компилируется
            SceneManager.class.getDeclaredConstructor().setAccessible(true);
            SceneManager.class.getDeclaredConstructor().newInstance();
        });
    }

    @Test
    void testRootConstantNotNull() {
        assertNotNull(SceneManager.ROOT);
    }
}
