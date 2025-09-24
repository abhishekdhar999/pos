package org.example.pojo;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class InvoicePojo  {
    private Integer id;
    private Integer orderId;
    private ZonedDateTime createdAt;
    private String status;
    private Double amount;
    private String pdfPath;
}
