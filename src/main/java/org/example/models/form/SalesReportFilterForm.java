package org.example.models.form;

import org.example.utils.FinalValues;

import java.time.ZonedDateTime;

public class SalesReportFilterForm {
    private Integer page;
    private Integer size;
    private String startDate = FinalValues.START_DATE;
    private String endDate = ZonedDateTime.now().toString();
    private String client = "";
    private String productBarcode = "";
}
