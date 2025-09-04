package org.example.dto;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {
    public ApiException(String message) {
        super(message);
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String nameCannotBeEmpty, HttpStatus httpStatus, String s, String s1) {
        super(s1);
    }
}

