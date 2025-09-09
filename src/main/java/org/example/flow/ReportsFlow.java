package org.example.flow;

import org.example.models.form.DaySalesReportsForm;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Service
public class ReportsFlow {

    public void generateDayReport(){
//        get orders of the day
        ZonedDateTime dateTime = ZonedDateTime.now();
        ZonedDateTime startDate = dateTime.minusDays(1).with(LocalTime.of(0,0,0));
        ZonedDateTime endDate = dateTime.minusDays(1).with(LocalTime.of(23,59,59));

        DaySalesReportsForm daySalesReportsForm = new DaySalesReportsForm();


    }
}
