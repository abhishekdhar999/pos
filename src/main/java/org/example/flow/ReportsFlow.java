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

import static org.example.dto.DtoHelper.createDaySalesReportPojo;

@Service
public class ReportsFlow {

    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;
    @Autowired
    private InvoiceClient invoiceClient;
    @Autowired
    private SalesReportApi salesReportApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ClientApi clientApi;

//    todo refine the code make most of it in report dto
    public void generateDayReport( DaySalesReportsForm daySalesReportsForm) throws ApiException {
        List<DaySalesReportPojo> dailySalesReportPojoList = salesReportApi.getDaySalesReports(daySalesReportsForm);
        if(!Objects.isNull(dailySalesReportPojoList) && !dailySalesReportPojoList.isEmpty()){
           System.out.println("already updated the daily sales report");
            return;
        }
        ZonedDateTime startDate = ZonedDateTime.parse(daySalesReportsForm.getStartDate());
        ZonedDateTime endDate = ZonedDateTime.parse(daySalesReportsForm.getEndDate());
    //todo remove the invoice call get it from orders
        List<OrderPojo> orderPojoList = orderApi.getOrderBetweenDatesStatusFulfillable(startDate,endDate);

        int orderCount = 0;
        int totalOrderCount = 0;
        Double totalOrderRevenue = 0.0;
        for(OrderPojo orderPojo : orderPojoList){
            orderCount++;
            List<OrderItemPojo> listOfOrderItem = orderItemApi.getByOrderId(orderPojo.getId());
            totalOrderCount += listOfOrderItem.size();
            for(OrderItemPojo orderItemPojo : listOfOrderItem){
                totalOrderRevenue += orderItemPojo.getSellingPrice()*orderItemPojo.getQuantity();
            }
        }

       DaySalesReportPojo daySalesReportPojo =  createDaySalesReportPojo(startDate,totalOrderRevenue,totalOrderCount,orderCount);
        salesReportApi.addDaySalesReport(daySalesReportPojo);

    }
//    todo mak e direct call from dto
//public List<DaySalesReportPojo> getDaysSalesReport(DaySalesReportsForm form) throws ApiException {
//        return  salesReportApi.getDaySalesReports(form);
//}

public Long getTotalDayReports(){
        return salesReportApi.getTotalDayReports();
}

//public List<DaySalesReportPojo> getDaySalesReportsBetweenDates(ExportFilterDailyReports form) throws ApiException {
//        ZonedDateTime startDate = ZonedDateTime.parse(form.getStartDate());
//        ZonedDateTime endDate = ZonedDateTime.parse(form.getEndDate());
//return salesReportApi.getDaySalesReportsBetweenDates(startDate,endDate);
//}
//todo break down into small methods
public List<SalesReportData> getSalesReport(SalesReportFilterForm salesReportFilterForm) throws ApiException {
        ZonedDateTime startDate = ZonedDateTime.parse(salesReportFilterForm.getStartDate());
        ZonedDateTime endDate = ZonedDateTime.parse(salesReportFilterForm.getEndDate());
        List<OrderPojo> listOfOrderPojo = orderApi.getOrderBetweenDatesStatusFulfillable(startDate,endDate);
         HashMap<Integer,Integer> itemCountMap = new HashMap<>();
         HashMap<Integer,Double> revenueMap = new HashMap<>();
       for(OrderPojo order : listOfOrderPojo){
           List<OrderItemPojo> listOfOrderItem = orderItemApi.getByOrderId(order.getId());
           for(OrderItemPojo orderItem : listOfOrderItem){
               itemCountMap.put(orderItem.getProductId(),itemCountMap.getOrDefault(orderItem.getProductId(),0)+orderItem.getQuantity());
               revenueMap.put(orderItem.getProductId(),revenueMap.getOrDefault(orderItem.getProductId(),0.0) + (orderItem.getQuantity() * orderItem.getSellingPrice()));
           }
       }
       List<SalesReportData> listSalesReportData = createListOfSalesReportData(revenueMap,itemCountMap);
        List<SalesReportData> listSalesReportDataFiltered =  createFilteredSalesReportData(listSalesReportData, salesReportFilterForm);
        return listSalesReportDataFiltered;
}


    public List<SalesReportData> createFilteredSalesReportData (List<SalesReportData> listSalesReportData,SalesReportFilterForm salesReportFilterForm){
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

    public List<SalesReportData> createListOfSalesReportData(HashMap<Integer,Double> revenueMap,HashMap<Integer,Integer> itemCountMap) throws ApiException {

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
        return listSalesReportData;
    }






}
