package org.example.flow;

import org.example.InvoiceClient.InvoiceClient;
import org.example.api.OrderApi;
import org.example.api.OrderItemApi;
import org.example.api.SalesReportApi;
import org.example.dto.ApiException;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.InvoiceData;
import org.example.models.data.SalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.models.form.ExportFilterDailyReports;
import org.example.models.form.SalesReportFilterForm;
import org.example.pojo.DaySalesReportPojo;
import org.example.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ReportsFlow {

    @Autowired
    private OrderApi orderApi;
    private final InvoiceClient invoiceClient;
    @Autowired
    private OrderItemApi orderItemApi;

    @Autowired
    private SalesReportApi salesReportApi;
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
//public List<SalesReportData> getSalesReport(SalesReportFilterForm formFilter) throws ApiException {
//        List<SalesReportData> salesReportDataList = new ArrayList<>();
//
//}




}
