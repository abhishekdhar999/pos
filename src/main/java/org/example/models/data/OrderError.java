package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderError {
    private Integer index;
    private String barcode;
    private String message;


}
