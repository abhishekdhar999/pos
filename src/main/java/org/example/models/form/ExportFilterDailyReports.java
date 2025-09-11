package org.example.models.form;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
@Getter
@Setter
public class ExportFilterDailyReports {

    String endDate = ZonedDateTime.now().toString();
    String startDate = ZonedDateTime.now().minusDays(30).toString();
}
