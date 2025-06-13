package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LastModelStorageTest {

    @Test
    void saveAndLoadLastModelId() {
        long id = 12345L;
        LastModelStorage.saveLastModelId(id);
        long loaded = LastModelStorage.loadLastModelId();
        assertThat(loaded).isEqualTo(id);

        // Проверка дефолтного значения (если очистить Preferences — тут опционально)
        // Можно вызвать метод напрямую с новым ключом, но обычно этот тест достаточно.
    }
}
