package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ReportsDto;
import org.example.models.data.DaySalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.utils.FinalValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Api
@RestController
@RequestMapping(value = "/api/reports")
public class ReportsController {

//    @Autowired
//    private ReportsDto reportsDto;
//    @Autowired
//    @ApiOperation("get daily reports")
//    @RequestMapping(value = "/daily",method = RequestMethod.GET)
//    public List<DaySalesReportData>  getDailyReports(@ModelAttribute DaySalesReportsForm form) {
//        if(form.getStartDate().isEmpty()) {
//            form.setStartDate(FinalValues.START_DATE);
//        }
//        if(form.getEndDate().isEmpty()) {
//            form.setEndDate(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        }
//
//        return reportsDto.getDaysSalesReports(form)
//    }


}
