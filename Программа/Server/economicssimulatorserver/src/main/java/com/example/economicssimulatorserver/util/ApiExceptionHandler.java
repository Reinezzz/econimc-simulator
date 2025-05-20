package com.example.economicssimulatorserver.exception;

import com.example.economicssimulatorserver.dto.ApiResponse;
import com.example.economicssimulatorserver.util.LocalizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ApiResponse> handleLocalizedException(
            LocalizedException ex, Locale locale) {

        String code = ex.getCode();
        String message;
        try {
            message = messageSource.getMessage(code, ex.getArgs(), locale);
        } catch (Exception e) {
            logException(ex);
            message = code; // если нет локализации — только код
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, message));
    }

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
                            return errCode; // если нет перевода — код ошибки
                        }
                    })
                    .collect(Collectors.joining("; "));
        } catch (Exception e) {
            logException(e);
            message = "error.validation";
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, message));
    }

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
        // Используй свой логгер
        ex.printStackTrace();
    }
}
