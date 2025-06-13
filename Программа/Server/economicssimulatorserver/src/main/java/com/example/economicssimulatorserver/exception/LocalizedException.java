package com.example.economicssimulatorserver.exception;

import lombok.Getter;

/**
 * Базовое unchecked-исключение с поддержкой локализации сообщения. <br>
 * Класс хранит код сообщения и его параметры, которые будут переданы в
 * {@link org.springframework.context.MessageSource} для получения
 * локализованного текста. Сам код сообщения также используется в качестве
 * текста исключения.
 */
@Getter
public class LocalizedException extends RuntimeException {

    /**
     * Код локализованного сообщения.
     */
    private final String code;

    /**
     * Параметры, передаваемые в сообщение.
     */
    private final Object[] args;

    /**
     * Создаёт новое исключение.
     *
     * @param code код сообщения в ресурсах локализации
     * @param args параметры для подстановки в сообщение
     */
    public LocalizedException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args;
    }
}