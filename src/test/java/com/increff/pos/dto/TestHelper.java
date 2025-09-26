package com.increff.pos.dto;

import com.sun.istack.NotNull;
import org.example.enums.OrderStatus;
import org.example.models.data.InvoiceData;
import org.example.models.data.InvoiceItem;
import org.example.models.data.InvoiceRequest;
import org.example.models.form.*;
import org.example.pojo.ClientPojo;
import org.example.pojo.DaySalesReportPojo;
import org.example.pojo.OrderPojo;
import org.example.utils.FinalValues;
import org.example.utils.InvoiceResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestHelper {

    public static ClientForm createClientForm(String name){
        ClientForm form = new ClientForm();
        form.setName(name);
        return form;
    }
    public static ClientForm createUpdateClientForm( String name){
        ClientForm updateForm = new ClientForm();
        updateForm.setName(name);
        return updateForm;
    }
public static ClientPojo CreateClientPojo(String name){
        ClientPojo clientPojo = new ClientPojo();
        clientPojo.setName(name);
        return clientPojo;
}
//    product test dto helper
    public static ProductForm createProductForm(String barcode,String clientName,String name,Double price,String imageUrl){
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setClientName(clientName);
        productForm.setName(name);
        productForm.setPrice(price);
        productForm.setImageUrl(imageUrl);
        return productForm;

    }

//    inventory test dto helper
    public static InventoryForm createInventoryForm(String barcode,Integer quantity){
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(barcode);
        inventoryForm.setQuantity(quantity);
        return inventoryForm;
    }

//    order test dto helper
    public static OrderItemForm createOrderItemForm(String barcode,Integer quantity,Double price){
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setQuantity(quantity);
        orderItemForm.setSellingPrice(price);
        orderItemForm.setBarcode(barcode);
        return orderItemForm;
    }

    public static OrderPojo createOrderPojo() {
        OrderPojo pojo = new OrderPojo();
        pojo.setDateTime(ZonedDateTime.now(ZoneId.of("UTC")));
        pojo.setStatus(OrderStatus.CREATED);
        return pojo;
    }

    public static OrderFiltersForm createOrderFilters(Integer page, Integer size, String startDate, String endDate) {
        OrderFiltersForm filters = new OrderFiltersForm();
        filters.setPage(page);
        filters.setSize(size);
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setStatus("");
        return filters;
    }

//    reports dto test helper
    public static SalesReportFilterForm createSalesReportFilterForm(Integer page, Integer size, String startDate, String endDate,String client,String barcode ) {
        SalesReportFilterForm filters = new SalesReportFilterForm();
        filters.setPage(page);
        filters.setSize(size);
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setClient(client);
        filters.setProductBarcode(barcode);
        return filters;
    }

    public static DaySalesReportPojo createDailySalesReportPojo(ZonedDateTime dateTime, Integer invoicedOrdersCount, Integer invoicedItemsCount, Double totalRevenue) {
        DaySalesReportPojo pojo = new DaySalesReportPojo();
        pojo.setDateTime(dateTime);
        pojo.setInvoicedOrdersCount(invoicedOrdersCount);
        pojo.setInvoicedItemsCount(invoicedItemsCount);
        pojo.setTotalRevenue(totalRevenue);
        return pojo;
    }
    public static DaySalesReportsForm createDaySalesReportForm(Integer page, Integer size, String startDate, String endDate) {
        DaySalesReportsForm form = new DaySalesReportsForm();
        form.setPage(page);
        form.setSize(size);
        form.setStartDate(startDate);
        form.setEndDate(endDate);
        return form;
    }


    public static DaySalesReportsForm createDaySalesReportForm(String startDate, String endDate, Integer page, Integer size) {
        DaySalesReportsForm form = new DaySalesReportsForm();
        form.setStartDate(startDate);
        form.setEndDate(endDate);
        form.setPage(page);
        form.setSize(size);
        return form;
    }

//    invoice dto helper test
    public static InvoiceItem createInvoiceItem(String productName,Integer quantity,Double price,Double total){
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setProductName(productName);
        invoiceItem.setPrice(price);
        invoiceItem.setQuantity(quantity);
        invoiceItem.setTotal(total);
        return invoiceItem;
    }

    public static InvoiceRequest createInvoiceRequest(Integer orderId,String orderDateTime,String invoiceDateTime,Double total,List<InvoiceItem> invoiceItems){
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setOrderId(orderId);
        invoiceRequest.setOrderDateTime(orderDateTime);
        invoiceRequest.setInvoiceDateTime(invoiceDateTime);
        invoiceRequest.setTotal(total);
        invoiceRequest.setInvoiceItems(invoiceItems);
        return invoiceRequest;
    }

    public static InvoiceData createInvoiceData(Integer id,Integer orderId,String createdAt,String status,Double amount,String pdfPath){
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setCreatedAt(createdAt);
        invoiceData.setId(id);
        invoiceData.setStatus(status);
        invoiceData.setOrderId(orderId);
        invoiceData.setPdfPath(pdfPath);
        invoiceData.setAmount(amount);
        return invoiceData;
    }

    public static OrderPojo createOrderPojo(boolean isInvoiced) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setId(1);
        orderPojo.setIsInvoiced(isInvoiced);
        orderPojo.setStatus(OrderStatus.CREATED);
        return orderPojo;
    }

    public static InvoiceResponse createInvoiceResponse(Integer invoiceId, String base64Pdf, String filePath) {
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoiceId(invoiceId);
        response.setBase64Pdf(base64Pdf);
        response.setFilePath(filePath);
        return response;
    }

    public static InvoiceFilterForm createInvoiceFilterForm() {
        InvoiceFilterForm form = new InvoiceFilterForm();
        form.setStartDate("2025-01-01T00:00:00Z");
        form.setEndDate("2025-01-31T23:59:59Z");
        return form;
    }

    public static List<InvoiceData> createInvoiceDataList() {
        List<InvoiceData> invoices = new ArrayList<>();
        InvoiceData invoice1 = new InvoiceData();
        invoice1.setId(1);
        invoice1.setOrderId(101);
        invoice1.setAmount(1000.0);
        invoice1.setStatus("CREATED");
        invoice1.setCreatedAt("2025-01-15T10:30:00Z");
        invoice1.setPdfPath("/invoices/invoice1.pdf");
        invoices.add(invoice1);

        InvoiceData invoice2 = new InvoiceData();
        invoice2.setId(2);
        invoice2.setOrderId(102);
        invoice2.setAmount(1500.0);
        invoice2.setStatus("CREATED");
        invoice2.setCreatedAt("2025-01-20T14:45:00Z");
        invoice2.setPdfPath("/invoices/invoice2.pdf");
        invoices.add(invoice2);

        return invoices;
    }


}
