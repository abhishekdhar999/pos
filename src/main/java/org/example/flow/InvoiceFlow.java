
package org.example.flow;



import lombok.Setter;
import org.example.InvoiceClient.InvoiceClient;
import org.example.api.ClientApi;
import org.example.api.OrderApi;
import org.example.api.OrderItemApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.models.data.InvoiceData;
import org.example.models.data.InvoiceItem;
import org.example.models.data.InvoiceRequest;
import org.example.models.form.InvoiceFilterForm;
import org.example.pojo.InvoicePojo;
import org.example.pojo.OrderItemPojo;
import org.example.pojo.OrderPojo;
import org.example.pojo.ProductPojo;
import org.example.utils.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Setter
@Transactional(rollbackFor = ApiException.class)
public class InvoiceFlow {
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InvoiceClient invoiceClient;
    @Autowired
    private ClientApi clientApi;

//    todo  make seprate methods for genrate invoice
    public InvoiceResponse generateInvoice(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderApi.getById(orderId);
        if (Objects.isNull(orderPojo)) {
            throw new ApiException("Order doesn't exist.");
        }
        Double total = 0.0;
        List<OrderItemPojo> orderItemPojoList = orderItemApi.getByOrderId(orderId);
        List<InvoiceItem> invoiceItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            Double subTotal = (orderItemPojo.getSellingPrice() * orderItemPojo.getQuantity());
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setTotal(subTotal);
            invoiceItem.setPrice(orderItemPojo.getSellingPrice());
            invoiceItem.setQuantity(orderItemPojo.getQuantity());
            ProductPojo productPojo = productApi.getById(orderItemPojo.getProductId());
            invoiceItem.setProductName(productPojo.getName());
            invoiceItemDataList.add(invoiceItem);
            total += subTotal;
        }
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setTotal(total);
        invoiceRequest.setInvoiceItems(invoiceItemDataList);
        invoiceRequest.setOrderId(orderId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z");
        invoiceRequest.setOrderDateTime(orderPojo.getDateTime().format(formatter));
        invoiceRequest.setInvoiceDateTime(ZonedDateTime.now().format(formatter));

        return invoiceClient.generateInvoice(invoiceRequest);
    }


    public List<InvoiceData> getInvoices(InvoiceFilterForm filterForm) throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.parse(filterForm.getStartDate());
        ZonedDateTime endDate = ZonedDateTime.parse(filterForm.getEndDate());
       return invoiceClient.getInvoice(startDate, endDate);
    }

}
