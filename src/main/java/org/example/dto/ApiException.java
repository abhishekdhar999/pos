package org.example.dto;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {
    private final HttpStatus status;
    private static final long serialVersionUID = 1L;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }



}
