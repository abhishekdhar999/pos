package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationResponse<T> {
    private T data;
    private String message;
}
