package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItem {
    private String productName;
    private Integer quantity;
    private Double price;
    private Double total;
}
