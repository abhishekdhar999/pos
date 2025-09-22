package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.ReportsDto;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.SalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.models.form.ExportFilterDailyReports;
import org.example.models.form.SalesReportFilterForm;
import org.example.utils.FinalValues;
import org.example.utils.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Api
@RestController
@RequestMapping(value = "/api/reports")
public class ReportsController {

    @Autowired
    private ReportsDto reportsDto;

    @ApiOperation("get daily reports")
    @RequestMapping(value = "/daily",method = RequestMethod.GET)
    public PaginatedResponse<DaySalesReportData>  getDailyReports(@ModelAttribute DaySalesReportsForm form) throws ApiException {
        if(form.getStartDate() == null || form.getStartDate().isEmpty()) {
            form.setStartDate(ZonedDateTime.now().toString());
        }
        if(form.getEndDate().isEmpty()) {
            form.setEndDate(ZonedDateTime.now().toString());
        }
        List<DaySalesReportData> listOfDaySalesReportData = reportsDto.getDaysSalesReports(form);
        Long total = (long)listOfDaySalesReportData.size();
        PaginatedResponse<DaySalesReportData> response = new PaginatedResponse<>();
        response.setPage(form.getPage());
        response.setSize(form.getSize());
        response.setData(listOfDaySalesReportData);
        response.setTotalPages(total / form.getSize() + 1);

return response;
    }

    @ApiOperation("export daily sales report")
    @RequestMapping(path = "/export",method = RequestMethod.GET)
    public String exportDailyReports(@ModelAttribute ExportFilterDailyReports form, HttpServletResponse response) throws ApiException, IOException {

      ZonedDateTime endDate = ZonedDateTime.now();

      if(form.getStartDate() != null && !form.getStartDate().isEmpty()) {
         ZonedDateTime startDate = ZonedDateTime.parse(form.getStartDate());
         if(startDate.isBefore(endDate.minusDays(31))) {
             throw  new ApiException("start date should be between the 30 days till now");
         }
      }
        if(form.getStartDate() != null && !form.getStartDate().isEmpty()) {
            ZonedDateTime startDate = ZonedDateTime.parse(form.getStartDate());
            if (startDate.isAfter(endDate)) {
                throw new ApiException("start date cannot be in the future");
            }
        }
      return  reportsDto.getDaySalesReportsBetweenDates(form, response);

    }
    @ApiOperation("getting sales report on filters")
    @RequestMapping(path = "/sales", method = RequestMethod.GET)
    public PaginatedResponse<SalesReportData> getSalesReport(@ModelAttribute SalesReportFilterForm salesReportFilterForm) throws ApiException {

        PaginatedResponse<SalesReportData> response = new PaginatedResponse<>();
        response.setData(reportsDto.getSalesReport(salesReportFilterForm));
        response.setPage(salesReportFilterForm.getPage());
        response.setSize(salesReportFilterForm.getSize());

        Long total = (long) response.getData().size();
        response.setTotalPages(total / salesReportFilterForm.getSize());
        
        return response;
    }

    @ApiOperation("get sales report ")
    @RequestMapping(path = "/export-sales",method = RequestMethod.GET)
    public String exportSalesReport(@ModelAttribute SalesReportFilterForm salesReportFilterForm) throws ApiException {
        return reportsDto.exportSalesReport(salesReportFilterForm);
    }





}
