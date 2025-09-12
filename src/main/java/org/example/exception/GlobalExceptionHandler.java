package org.example.exception;

import org.example.dto.ApiException;
import org.example.utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NestedServletException.class)
    public ResponseEntity<ErrorResponse> handleNestedServletException(NestedServletException ex, HttpServletRequest request) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof ApiException) {
            ApiException apiException = (ApiException) rootCause;
            ErrorResponse response = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    apiException.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // If it's not an ApiException, handle as generic error
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error: " + rootCause.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "An error occurred: " + ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid argument: " + ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Runtime error: " + ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", request.getDescription(false));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

