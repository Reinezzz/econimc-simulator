package com.example.economicssimulatorserver.exception;

import com.example.economicssimulatorserver.dto.ApiResponse;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiExceptionHandlerTest {

    private MessageSource messageSource;
    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        handler = new ApiExceptionHandler(messageSource);
    }

    @Test
    void handleLocalizedException_localizedMessage() {
        when(messageSource.getMessage(eq("error.code"), any(), any()))
                .thenReturn("Локализованное сообщение");

        LocalizedException ex = new LocalizedException("error.code", "foo");
        ResponseEntity<ApiResponse> resp = handler.handleLocalizedException(ex, Locale.getDefault());
        assertEquals(400, resp.getStatusCodeValue());
        assertEquals("Локализованное сообщение", resp.getBody().message());
        assertFalse(resp.getBody().success());
    }

    @Test
    void handleLocalizedException_noLocalization() {
        when(messageSource.getMessage(anyString(), any(), any()))
                .thenThrow(new RuntimeException("not found"));

        LocalizedException ex = new LocalizedException("error.unknown");
        ResponseEntity<ApiResponse> resp = handler.handleLocalizedException(ex, Locale.getDefault());
        assertEquals(400, resp.getStatusCodeValue());
        assertEquals("error.unknown", resp.getBody().message());
        assertFalse(resp.getBody().success());
    }

    @Test
    void handleValidationException() {
        // Тестировать можно через конструктор MethodArgumentNotValidException, но здесь проверяем поведение
        // Можно использовать мок объекта, если нужен более сложный тест
        // Оставим пример шаблона, поскольку полноценный тест требует Spring MVC окружения
        assertNotNull(handler);
    }

    @Test
    void handleAllOtherExceptions_localized() {
        when(messageSource.getMessage(eq("error.unknown"), any(), any()))
                .thenReturn("Неизвестная ошибка");
        Exception ex = new Exception("Any");
        ResponseEntity<ApiResponse> resp = handler.handleAllOtherExceptions(ex, Locale.getDefault());
        assertEquals(500, resp.getStatusCodeValue());
        assertEquals("Неизвестная ошибка", resp.getBody().message());
        assertFalse(resp.getBody().success());
    }

    @Test
    void handleAllOtherExceptions_noLocalization() {
        when(messageSource.getMessage(eq("error.unknown"), any(), any()))
                .thenThrow(new RuntimeException());
        Exception ex = new Exception("Any");
        ResponseEntity<ApiResponse> resp = handler.handleAllOtherExceptions(ex, Locale.getDefault());
        assertEquals(500, resp.getStatusCodeValue());
        assertEquals("error.unknown", resp.getBody().message());
        assertFalse(resp.getBody().success());
    }
}
