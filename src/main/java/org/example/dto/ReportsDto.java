package org.example.dto;

import org.example.flow.OrderFlow;
import org.example.flow.ReportsFlow;
import org.example.models.data.DaySalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportsDto {
    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private ReportsFlow reportsFlow;
//    public List<DaySalesReportData> getDaysSalesReports(DaySalesReportsForm form) throws ApiException{
//
//        List<DaySalesReportData> daySalesReports =  reportApi.getDaysSalesReport()
//    }

   public void generateDayReports() throws ApiException {
      reportsFlow.generateDayReport();
   }
}
