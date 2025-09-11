package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {
    private T data;
    private String message;
}
