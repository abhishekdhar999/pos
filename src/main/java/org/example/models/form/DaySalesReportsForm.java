package org.example.models.form;

import lombok.Getter;
import lombok.Setter;
import org.example.utils.FinalValues;

import java.time.ZonedDateTime;

@Getter
@Setter
public class DaySalesReportsForm {
    private Integer page;
    private Integer size;
    private String startDate = FinalValues.START_DATE;
    private String endDate = ZonedDateTime.now().toString();
}
