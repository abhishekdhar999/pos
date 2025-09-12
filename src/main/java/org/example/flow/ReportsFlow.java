package org.example.flow;

import org.example.InvoiceClient.InvoiceClient;
import org.example.api.*;
import org.example.dto.ApiException;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.InvoiceData;
import org.example.models.data.SalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.models.form.ExportFilterDailyReports;
import org.example.models.form.SalesReportFilterForm;
import org.example.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportsFlow {

    @Autowired
    private OrderApi orderApi;
    private final InvoiceClient invoiceClient;
    @Autowired
    private OrderItemApi orderItemApi;

    @Autowired
    private SalesReportApi salesReportApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ClientApi clientApi;

    public ReportsFlow(InvoiceClient invoiceClient) {
        this.invoiceClient = invoiceClient;
    }

    public void generateDayReport() throws ApiException {
//        get orders of the day
        ZonedDateTime dateTime = ZonedDateTime.now();
        ZonedDateTime startDate = dateTime.minusDays(1).with(LocalTime.of(0,0,0));
        ZonedDateTime endDate = dateTime.minusDays(1).with(LocalTime.of(23,59,59));

        DaySalesReportsForm daySalesReportsForm = new DaySalesReportsForm();
daySalesReportsForm.setStartDate(startDate.format(DateTimeFormatter.ISO_DATE_TIME));
daySalesReportsForm.setEndDate(endDate.format(DateTimeFormatter.ISO_DATE_TIME));
daySalesReportsForm.setPage(0);
daySalesReportsForm.setSize(1);

        List<DaySalesReportPojo> dailySalesReportPojoList = salesReportApi.getDaySalesReports(daySalesReportsForm);
        if(!Objects.isNull(dailySalesReportPojoList) && !dailySalesReportPojoList.isEmpty()){
           System.out.println("already done");
            // it means that we have already updated the daily sales report
            return;
        }
System.out.println(startDate.format(DateTimeFormatter.ISO_DATE_TIME));
System.out.println(endDate.format(DateTimeFormatter.ISO_DATE_TIME));
        List<InvoiceData> invoiceData = invoiceClient.getInvoice(startDate, endDate);

        if(Objects.isNull(invoiceData) || invoiceData.isEmpty()){
            System.out.println("no invoice data");
            return;
        }
        for(InvoiceData invoice : invoiceData){
            System.out.println(invoice.getOrderId());
            System.out.println(invoice.getId());
        }
        int invoiceOrderCount = 0;
        int invoiceItemCount = 0;
        Double totalAmount = 0.0;

        for (InvoiceData invoice : invoiceData) {
            List<OrderItemPojo> listOfOrderItem =  orderItemApi.getByOrderId(invoice.getOrderId());
            invoiceOrderCount++;
            invoiceItemCount += listOfOrderItem.size();

            for(OrderItemPojo orderItem : listOfOrderItem){
                totalAmount += orderItem.getSellingPrice()*orderItem.getQuantity();
            }
        }

        DaySalesReportPojo daySalesReportPojo = new DaySalesReportPojo();
        daySalesReportPojo.setDateTime(startDate);
        daySalesReportPojo.setTotalRevenue(totalAmount);
        daySalesReportPojo.setInvoicedOrdersCount(invoiceOrderCount);
        daySalesReportPojo.setInvoicedItemsCount(invoiceItemCount);
        salesReportApi.addDaySalesReport(daySalesReportPojo);

    }
public List<DaySalesReportPojo> getDaysSalesReport(DaySalesReportsForm form) throws ApiException {
        return  salesReportApi.getDaySalesReports(form);
}

public Long getTotalDayReports(){
        return salesReportApi.getTotalDayReports();
}

public List<DaySalesReportPojo> getDaySalesReportsBetweenDates(ExportFilterDailyReports form) throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.parse(form.getStartDate());
        ZonedDateTime endDate = ZonedDateTime.parse(form.getEndDate());
return salesReportApi.getDaySalesReportsBetweenDates(startDate,endDate);
}

public List<SalesReportData> getSalesReport(SalesReportFilterForm salesReportFilterForm) throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.parse(salesReportFilterForm.getStartDate());
        ZonedDateTime endDate = ZonedDateTime.parse(salesReportFilterForm.getEndDate());
        List<OrderPojo> listOfOrderPojo = orderApi.getBetweenDates(startDate,endDate);

         HashMap<Integer,Integer> itemCountMap = new HashMap<>();
         HashMap<Integer,Double> revenueMap = new HashMap<>();


       for(OrderPojo order : listOfOrderPojo){
           List<OrderItemPojo> listOfOrderItem = orderItemApi.getByOrderId(order.getId());
           for(OrderItemPojo orderItem : listOfOrderItem){

               itemCountMap.put(orderItem.getProductId(),itemCountMap.getOrDefault(orderItem.getProductId(),0)+orderItem.getQuantity());

               revenueMap.put(orderItem.getProductId(),revenueMap.getOrDefault(orderItem.getProductId(),0.0) + (orderItem.getQuantity() * orderItem.getSellingPrice()));
           }
       }

       List<SalesReportData> listSalesReportData = new ArrayList<>();
       for(Map.Entry<Integer, Integer> entry : itemCountMap.entrySet()){
           SalesReportData salesReportData = new SalesReportData();

//           product
           ProductPojo productPojo = productApi.getById(entry.getKey());
//           client
           ClientPojo clientPojo = clientApi.getById(productPojo.getClientId());

//           putting value
           salesReportData.setProductBarcode(productPojo.getBarcode());
           salesReportData.setClient(clientPojo.getName());
           salesReportData.setQuantity(entry.getValue());
            salesReportData.setRevenue(revenueMap.get(entry.getKey()));

            listSalesReportData.add(salesReportData);
       }

       List<SalesReportData> listSalesReportDataFilters = new ArrayList<>();

    if(salesReportFilterForm.getClient().isEmpty() && !salesReportFilterForm.getProductBarcode().isEmpty()){
//           only check for barcode
        for(SalesReportData salesReportData : listSalesReportData ){
if(salesReportData.getProductBarcode().contains(salesReportFilterForm.getProductBarcode())){
listSalesReportDataFilters.add(salesReportData);
}
        }
    }else if(salesReportFilterForm.getProductBarcode().isEmpty() &&  !salesReportFilterForm.getClient().isEmpty()){
//        only check for client
        for(SalesReportData salesReportData : listSalesReportData ){
            if(salesReportData.getClient().contains(salesReportFilterForm.getClient())){
                listSalesReportDataFilters.add(salesReportData);
            }
        }
    }else{
//        check for both client and barcode
        for(SalesReportData salesReportData : listSalesReportData ){
            if(salesReportData.getProductBarcode().contains(salesReportFilterForm.getProductBarcode()) && salesReportData.getClient().contains(salesReportFilterForm.getClient())){
                listSalesReportDataFilters.add(salesReportData);
            }
        }
    }

    return listSalesReportDataFilters;

}

public Long getTotalOrdersCount(){
        return orderApi.getCount();
}



}
