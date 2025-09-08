package org.example.dto;


import org.example.InvoiceClient.InvoiceClient;
import org.example.api.OrderApi;
import org.example.flow.InvoiceFlow;
import org.example.flow.OrderFlow;
import org.example.pojo.OrderPojo;
import org.example.utils.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Base64;

@Component
public class InvoiceDto {

    @Autowired
    private InvoiceFlow invoiceFlow;

    @Autowired
   private OrderFlow orderFlow;
    @Autowired
    private InvoiceClient invoiceClient;
    @Autowired
    private OrderApi orderApi;

    public void generateInvoice(int orderId, HttpServletResponse response) throws ApiException {


        InvoiceResponse res = invoiceFlow.generateInvoice(orderId);
        String base64Pdf = res.getBase64Pdf();
        System.out.println("base64Pdf: " + base64Pdf);
        try {
            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice-" + orderId + ".pdf"); // or inline;
            response.setContentLength(pdfBytes.length);
            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
           OrderPojo orderPojo =  orderFlow.getOrderById(orderId);
           orderPojo.setIsInvoiced(Boolean.TRUE);
            orderApi.updateOrder(orderPojo);
        } catch (Exception e) {
            throw new ApiException("Error while invoice generation");
        }
    }


}
