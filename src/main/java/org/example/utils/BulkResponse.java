package org.example.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkResponse<T> {
    private int index;          // Row number in the TSV (starting from 0 or 1)
    private boolean success;    // Whether operation succeeded
    private String message;     // Error/success message
    private T data;             // Optional: the actual object created/updated


}
