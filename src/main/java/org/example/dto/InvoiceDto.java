package org.example.dto;


import org.example.InvoiceClient.InvoiceClient;
import org.example.api.OrderApi;
import org.example.enums.OrderStatus;
import org.example.flow.InvoiceFlow;
import org.example.flow.OrderFlow;
import org.example.models.data.InvoiceData;
import org.example.models.form.InvoiceFilterForm;
import org.example.pojo.InvoicePojo;
import org.example.pojo.OrderPojo;
import org.example.utils.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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

    public InvoiceResponse generateInvoice(int orderId) throws ApiException {

        OrderPojo orderPojoo =  orderFlow.getOrderById(orderId);
        if(Objects.isNull(orderPojoo)){
            throw new ApiException("Order id not found");
        }
        if(orderPojoo.getIsInvoiced()){
            throw new ApiException("Order has already invoiced");
        }

        InvoiceResponse res = invoiceFlow.generateInvoice(orderId);
           OrderPojo orderPojo =  orderFlow.getOrderById(orderId);
           orderPojo.setIsInvoiced(Boolean.TRUE);
           if(orderPojo.getIsInvoiced() == Boolean.TRUE){
               orderPojo.setStatus(OrderStatus.INVOICED);
           }
            orderApi.updateOrder(orderPojo);
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setInvoiceId(res.getInvoiceId());

        return invoiceResponse;
    }

    public void downloadInvoice(Integer orderId,HttpServletResponse response) throws ApiException {
     InvoiceResponse downloadInvoiceResponse = invoiceClient.downloadInvoice(orderId);
System.out.println("downloadInvoiceResponse:"+downloadInvoiceResponse);
        byte[] pdfBytes = Base64.getDecoder().decode(downloadInvoiceResponse.getBase64Pdf());
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=myfile.pdf"); // or inline;
            response.setContentLength(pdfBytes.length);
            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
        } catch (Exception e) {
            throw new ApiException("Error while downloading invoice.");
        }
    }
    public List<InvoiceData> getInvoices(InvoiceFilterForm filterForm) throws ApiException {
        return invoiceFlow.getInvoices(filterForm);
    }


}
