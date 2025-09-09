package org.example.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponse {
    private Integer invoiceId;
    private String base64Pdf;
    private String filePath;
}
