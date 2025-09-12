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
            form.setStartDate(FinalValues.START_DATE);
        }
        if(form.getEndDate().isEmpty() ||  form.getEndDate() == null) {
            form.setEndDate(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        }

        List<DaySalesReportData> listOfDaySalesReportData = reportsDto.getDaysSalesReports(form);
        PaginatedResponse<DaySalesReportData> response = new PaginatedResponse<>();
        response.setPage(form.getPage());
        response.setSize(form.getSize());
        response.setData(listOfDaySalesReportData);
        response.setTotalPages(reportsDto.getTotalDayReports() / form.getSize() + 1);

return response;
    }

    @ApiOperation("export daily sales report")
    @RequestMapping(path = "/export",method = RequestMethod.GET)
    public void exportDailyReports(@ModelAttribute ExportFilterDailyReports form, HttpServletResponse response) throws ApiException, IOException {

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
        reportsDto.getDaySalesReportsBetweenDates(form, response);

    }
    @ApiOperation("get sales report bases on date range, client, product")
    @RequestMapping(path = "/sales", method = RequestMethod.GET)
    public PaginatedResponse<SalesReportData> getSalesReport(@ModelAttribute SalesReportFilterForm salesReportFilterForm) throws ApiException {

        PaginatedResponse<SalesReportData> response = new PaginatedResponse<>();
        response.setData(reportsDto.getSalesReport(salesReportFilterForm));
        response.setPage(salesReportFilterForm.getPage());
        response.setSize(salesReportFilterForm.getSize());
        response.setTotalPages(reportsDto.getTotalOrdersCount()/salesReportFilterForm.getSize()+1);
        return response;
    }





}
