package org.example.dto;

import org.example.flow.OrderFlow;
import org.example.flow.ReportsFlow;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.SalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.models.form.ExportFilterDailyReports;
import org.example.models.form.SalesReportFilterForm;
import org.example.pojo.DaySalesReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.dto.DtoHelper.convertDaySalesReportPojoToDaySalesReportData;
import static org.example.dto.DtoHelper.normalizeSalesReportFilterForm;

@Component
public class ReportsDto {
    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private ReportsFlow reportsFlow;


    public List<DaySalesReportData> getDaysSalesReports(DaySalesReportsForm form) throws ApiException{

        List<DaySalesReportPojo> daySalesReportsPojo =  reportsFlow.getDaysSalesReport(form);
       return convertDaySalesReportPojoToDaySalesReportData(daySalesReportsPojo);

    }

   public void generateDayReports() throws ApiException {
      reportsFlow.generateDayReport();
   }
   public Long getTotalDayReports() throws ApiException{
        return reportsFlow.getTotalDayReports();
   }
   public void getDaySalesReportsBetweenDates(ExportFilterDailyReports form, HttpServletResponse response) throws ApiException, IOException {
     List<DaySalesReportPojo> daySalesReportPojos =  reportsFlow.getDaySalesReportsBetweenDates(form);
     if(Objects.isNull(daySalesReportPojos)){
         throw new ApiException("error genertaeing the sales report data");
     }
     List<DaySalesReportData> daySalesReportDataList =  convertDaySalesReportPojoToDaySalesReportData(daySalesReportPojos);

       response.setContentType("text/tab-separated-values");
       response.setHeader("Content-Disposition", "attachment; filename=daySalesReport.tsv");
//       write tsv data
       PrintWriter writer = response.getWriter();
       writer.println("Date\tInvoicedOrderCount\tInvoicedItemCount\tTotalRevenue");
       for(DaySalesReportData daySalesReportData : daySalesReportDataList){
           writer.println(daySalesReportData.getDate() + "\t" + daySalesReportData.getInvoicedOrdersCount() + "\t" + daySalesReportData.getInvoicedItemsCount() + "\t" + daySalesReportData.getTotalRevenue());
       }
       writer.flush();

   }
   public List<SalesReportData> getSalesReport(SalesReportFilterForm salesReportFilterForm) throws ApiException {
        normalizeSalesReportFilterForm(salesReportFilterForm);
        return reportsFlow.getSalesReport(salesReportFilterForm);
   }

}
