package com.exam.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Chuyển RuntimeException thành JSON response đúng chuẩn
 * Thay vì trả về HTML error page (gây lỗi parse JSON ở frontend)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error", ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định",
                "message", ex.getMessage() != null ? ex.getMessage() : "Lỗi không xác định"
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "error", "Lỗi server: " + ex.getMessage(),
                "message", ex.getMessage() != null ? ex.getMessage() : "Lỗi server"
            ));
    }
}
