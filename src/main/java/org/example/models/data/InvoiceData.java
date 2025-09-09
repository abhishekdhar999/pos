package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class InvoiceData {
    private Integer id;
    private Integer orderId;
    private String createdAt;
    private String status;
    private Double amount;
    private String pdfPath;
}
