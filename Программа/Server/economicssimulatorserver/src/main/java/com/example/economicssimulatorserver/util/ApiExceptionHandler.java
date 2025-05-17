package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.dto.ApiResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse resp = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(401).body(resp);
    }
}
