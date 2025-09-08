package org.example.utils;

import java.io.FileOutputStream;
import java.util.Base64;

public class InvoiceUtil {
    public static String saveInvoicePdf(String base64, String filePath) throws Exception {
        byte[] pdfBytes = Base64.getDecoder().decode(base64);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }
        return filePath;
    }
}
