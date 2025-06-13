package com.example.economicssimulatorserver.exception;

import com.example.economicssimulatorserver.dto.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений REST-слоя приложения.
 * <p>
 * Преобразует возникшие исключения в {@link ApiResponse} c локализованными
 * сообщениями. Для локализации используется {@link MessageSource}, а при
 * отсутствии нужного сообщения возвращается сам код ошибки.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Источник сообщений для локализации ответов. */
    private final MessageSource messageSource;

    /**
     * Создаёт обработчик с указанным источником сообщений.
     *
     * @param messageSource {@link MessageSource} для поиска текстов ошибок
     */
    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Обрабатывает пользовательские исключения с кодом локализованного сообщения.
     *
     * @param ex     выброшенное исключение
     * @param locale текущая локаль клиента
     * @return {@link ApiResponse} с текстом ошибки
     */
    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ApiResponse> handleLocalizedException(
            LocalizedException ex, Locale locale) {

        String code = ex.getCode();
        String message;
        try {
            message = messageSource.getMessage(code, ex.getArgs(), locale);
        } catch (Exception e) {
            logException(ex);
            message = code;
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, message));
    }

    /**
     * Формирует ответ при ошибках валидации параметров запроса.
     *
     * @param ex     исключение валидации
     * @param locale локаль клиента
     * @return {@link ApiResponse} с описанием ошибок
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException ex, Locale locale) {

        String message;
        try {
            message = ex.getBindingResult().getAllErrors().stream()
                    .map(error -> {
                        String errCode = error.getDefaultMessage();
                        if (errCode == null) errCode = "error.validation";
                        try {
                            return messageSource.getMessage(errCode, null, locale);
                        } catch (Exception e) {
                            logException(e);
                            return errCode;
                        }
                    })
                    .collect(Collectors.joining("; "));
        } catch (Exception e) {
            logException(e);
            message = "error.validation";
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, message));
    }

    /**
     * Перехватывает все остальные необработанные исключения.
     *
     * @param ex     возникшее исключение
     * @param locale локаль клиента
     * @return ответ с кодом 500 и текстом неизвестной ошибки
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllOtherExceptions(
            Exception ex, Locale locale) {
        logException(ex);
        String code = "error.unknown";
        String message;
        try {
            message = messageSource.getMessage(code, null, locale);
        } catch (Exception e) {
            message = code;
        }
        return ResponseEntity.internalServerError().body(new ApiResponse(false, message));
    }

    private void logException(Exception ex) {
        ex.printStackTrace();
    }
}
