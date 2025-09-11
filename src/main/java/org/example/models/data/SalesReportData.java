package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportData {
    private String client;
    private String productBarcode;
    private Integer quantity;
    private Double revenue;
}
