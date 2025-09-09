package org.example.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponse {
    private Integer invoiceId;
    private String base64Pdf;
    private String filePath;
//    private Boolean isInvoiced =  false;
//    private String base64Pdf;
//    public String getBase64Pdf() { return base64Pdf; }
//    public void setBase64Pdf(String base64Pdf) { this.base64Pdf = base64Pdf; }
}
