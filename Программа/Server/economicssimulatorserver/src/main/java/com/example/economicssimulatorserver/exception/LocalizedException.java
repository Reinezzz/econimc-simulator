package com.example.economicssimulatorserver.exception;

import lombok.Getter;

/**
 * Исключение с поддержкой локализованных сообщений.
 * <p>
 * Используется для передачи в глобальный обработчик ошибок кода (ключа) сообщения и его аргументов.
 */
@Getter
public class LocalizedException extends RuntimeException {
    /** Ключ сообщения для поиска в ресурсах локализации. */
    private final String code;
    /** Аргументы для форматирования сообщения (опционально). */
    private final Object[] args;

    /**
     * Конструктор локализованного исключения.
     *
     * @param code ключ сообщения
     * @param args аргументы для подстановки (может быть пустым)
     */
    public LocalizedException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args;
    }
}
