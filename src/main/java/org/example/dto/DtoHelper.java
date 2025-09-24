package org.example.dto;

import org.example.models.data.*;
import org.example.models.form.*;
import org.example.pojo.*;
import org.example.utils.UtilMethods;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DtoHelper {

    public static List<DaySalesReportData> convertDaySalesReportPojoToDaySalesReportData(List<DaySalesReportPojo> list)throws ApiException{
        List<DaySalesReportData> daySalesReportDataList = new ArrayList<>();
        for(DaySalesReportPojo daySalesReportPojo : list){
            DaySalesReportData daySalesReportData = new DaySalesReportData();

            daySalesReportData.setDate(daySalesReportPojo.getDateTime().toString());
            daySalesReportData.setTotalRevenue(daySalesReportPojo.getTotalRevenue());
            daySalesReportData.setInvoicedItemsCount(daySalesReportPojo.getInvoicedItemsCount());
            daySalesReportData.setInvoicedOrdersCount(daySalesReportPojo.getInvoicedOrdersCount());
            daySalesReportDataList.add(daySalesReportData);
        }
        return daySalesReportDataList;
    }
    public static ClientPojo convertClientFormToClientPojo(ClientForm client){
        ClientPojo clientPojo = new ClientPojo();
        clientPojo.setName(client.getName());
        return clientPojo;
    }

    public static ClientData convertClientPojoToClientData(ClientPojo clientPojo){
        ClientData clientData = new ClientData();
        clientData.setId(clientPojo.getId());
        clientData.setName(clientPojo.getName());
        return clientData;
    }

    public static ProductPojo convertProductFormToProductPojo(ProductForm productForm, Integer clientId) {
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBarcode(productForm.getBarcode());
        productPojo.setPrice(productForm.getPrice());
        productPojo.setImageUrl(productForm.getImageUrl());
        productPojo.setName(productForm.getName().toLowerCase());
        productPojo.setClientId(clientId);
        return productPojo;
    }

    public static ProductData convertProductPojoToProductData(ProductPojo productPojo, String clientName, Integer inventory){
        ProductData productData = new ProductData();

        productData.setBarcode(productPojo.getBarcode());
        productData.setName(productPojo.getName());
        productData.setId(productPojo.getId());
        productData.setPrice(productPojo.getPrice());
        productData.setImageUrl(productPojo.getImageUrl());
        productData.setClientName(clientName);
        productData.setQuantity(inventory);

        return productData;
    }

    public static InventoryPojo convertInventoryFormToInventoryPojo(InventoryForm inventoryForm, Integer productId){
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setQuantity(inventoryForm.getQuantity());
        inventoryPojo.setProductId(productId);
        return inventoryPojo;
    }

    public static InventoryData convertInventoryPojoToInventoryData(InventoryPojo inventoryPojo, String barcode) {
        InventoryData inventoryData = new InventoryData();
        inventoryData.setId(inventoryPojo.getId());
        inventoryData.setBarcode(barcode);
        inventoryData.setQuantity(inventoryPojo.getQuantity());
        return inventoryData;
    }

    public static OrderItemPojo convertOrderFormToOrderItemPojo(OrderItemForm orderItemForm){
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setSellingPrice(orderItemForm.getSellingPrice());
        orderItemPojo.setQuantity(orderItemForm.getQuantity());
        return orderItemPojo;
    }

    public static OrderItemData convertOrderItemPojoToOrderItemData(OrderItemPojo orderItemPojo) {
        OrderItemData orderItemData = new OrderItemData();
        orderItemData.setId(orderItemPojo.getId());
        orderItemData.setQuantity(orderItemPojo.getQuantity());
        orderItemData.setProductId(orderItemPojo.getProductId());
        orderItemData.setSellingPrice(orderItemPojo.getSellingPrice());
        return orderItemData;
    }

    public static ProductForm convertProductPojoToProductForm(ProductPojo productPojo){
        ProductForm productForm = new ProductForm();

        productForm.setImageUrl(productPojo.getImageUrl());
        productForm.setName(productPojo.getName());
        productForm.setPrice(productPojo.getPrice());
        productForm.setBarcode(productPojo.getBarcode());

        return productForm;
    }

//    public static DailySalesReportData convertDailySalesReportPojoToData(DailySalesReportPojo dailySalesReportPojo) {
//        DailySalesReportData dailySalesReportData = new DailySalesReportData();
//        dailySalesReportData.setDate(dailySalesReportPojo.getDateTime().format(DateTimeFormatter.ISO_DATE));
//        dailySalesReportData.setTotalRevenue(dailySalesReportPojo.getTotalRevenue());
//        dailySalesReportData.setInvoicedItemsCount(dailySalesReportPojo.getInvoicedItemsCount());
//        dailySalesReportData.setInvoicedOrdersCount(dailySalesReportPojo.getInvoicedOrdersCount());
//        return dailySalesReportData;
//    }

    public static List<ClientData> convertClientPojoListToClientDataList(List<ClientPojo> clientPojoList) {
        List<ClientData> clientDataList = new ArrayList<>();
        for (ClientPojo clientPojo : clientPojoList) {
            ClientData clientData = convertClientPojoToClientData(clientPojo);
            clientDataList.add(clientData);
        }
        return clientDataList;
    }

    public static List<OrderItemPojo> convertOrderFormListToOrderItemPojoList(List<OrderItemForm> orderItemFormList) {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();
        for(OrderItemForm orderItemForm : orderItemFormList){
            UtilMethods.normalizeOrderForm(orderItemForm);
            orderItemPojoList.add(convertOrderFormToOrderItemPojo(orderItemForm));
        }
        return orderItemPojoList;
    }

    public static List<OrderItemData> convertOrderItemPojoListToOrderItemDataList(List<OrderItemPojo> orderItemPojoList) {
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo orderItemPojo: orderItemPojoList){
            OrderItemData orderItemData = convertOrderItemPojoToOrderItemData(orderItemPojo);
            orderItemDataList.add(orderItemData);
        }
        return orderItemDataList;
    }

    public static OrderData convertToOrderData(OrderPojo orderPojo, List<OrderItemData> orderItemDataList) {
        OrderData orderData = new OrderData();
        orderData.setId(orderPojo.getId());
        orderData.setStatus(orderPojo.getStatus());
        orderData.setIsInvoiced(orderPojo.getIsInvoiced());
        ZonedDateTime dateTime = orderPojo.getDateTime();
        orderData.setDateTime(dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        orderData.setOrderItems(orderItemDataList);
        return orderData;
    }

    public static void normalizeSalesReportFilterForm(SalesReportFilterForm salesReportFilterForm) {
        salesReportFilterForm.setClient(salesReportFilterForm.getClient().toLowerCase());
        salesReportFilterForm.setProductBarcode(salesReportFilterForm.getProductBarcode().toLowerCase());
    }

//    dto for orders
    public static void validateProductInOrderCreation(ProductPojo productPojo ,String barcode,OrderItemPojo orderItemPojo) throws ApiException {
        if (Objects.isNull(productPojo)) {
            throw new ApiException("Product with barcode '" + barcode + "' does not exist");
        }
        if (productPojo.getPrice() < orderItemPojo.getSellingPrice()) {
            throw new ApiException("Selling Price is higher than Price for product: " + barcode);
        }
    }

    public static void validateInventoryInOrderCreation(InventoryPojo inventory ,String barcode,OrderItemPojo orderItemPojo) throws ApiException {
        if (inventory == null || inventory.getQuantity() <= 0) {
            throw new ApiException("Product' with barcode '" + barcode + "' is out of stock");
        }
        if (inventory.getQuantity() < orderItemPojo.getQuantity()) {
            throw new ApiException("Only " + inventory.getQuantity() +
                    (inventory.getQuantity() == 1 ? " item is" : " items are") +
                    " left for product ' with barcode '" + barcode + "'");
        }
    }

    public static OrderError createError( String message){
        OrderError error = new OrderError();
        error.setMessage(message);
        return error;
    }

//    reports dto
public static DaySalesReportPojo createDaySalesReportPojo(ZonedDateTime startDate,Double totalAmount, int invoiceOrderCount, int invoiceItemCount){
    DaySalesReportPojo daySalesReportPojo = new DaySalesReportPojo();
    daySalesReportPojo.setDateTime(startDate);
    daySalesReportPojo.setTotalRevenue(totalAmount);
    daySalesReportPojo.setInvoicedOrdersCount(invoiceOrderCount);
    daySalesReportPojo.setInvoicedItemsCount(invoiceItemCount);
    return daySalesReportPojo;
}

    public static DaySalesReportsForm createDaySalesReportForm(ZonedDateTime startDate,ZonedDateTime endDate){
        DaySalesReportsForm daySalesReportsForm = new DaySalesReportsForm();
        daySalesReportsForm.setStartDate(startDate.format(DateTimeFormatter.ISO_DATE_TIME));
        daySalesReportsForm.setEndDate(endDate.format(DateTimeFormatter.ISO_DATE_TIME));
        daySalesReportsForm.setPage(0);
        daySalesReportsForm.setSize(1);
        return daySalesReportsForm;
    }
}
