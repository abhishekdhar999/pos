package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceRequest {
    private Integer orderId;
    private String orderDateTime;
    private String invoiceDateTime;
    private Double total;
    private List<InvoiceItem> invoiceItems;
}
