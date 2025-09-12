package org.example.models.form;

import lombok.Getter;
import lombok.Setter;
import org.example.utils.FinalValues;

import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesReportFilterForm {
    private String endDate = ZonedDateTime.now().toString();
    private String startDate = ZonedDateTime.now().minusDays(30).toString();
    private String client = "";
    private String productBarcode = "";
    private Integer page;
    private Integer size;
}
