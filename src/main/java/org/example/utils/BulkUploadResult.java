package org.example.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkUploadResult<T> {
    private int successCount;                 // number of successful rows
    private int failureCount;                 // number of failed rows
    private List<BulkResponse<T>> failures;   // only failed rows

    // getters and setters
}
