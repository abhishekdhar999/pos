package org.example.models.form;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class InvoiceFilterForm {
    private Integer page = 0;
    private Integer size = 10;
    private String startDate = "2025-09-01T10:00:00.500+05:30";
    private String endDate = ZonedDateTime.now().toString();

}
