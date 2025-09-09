package org.example.Schedular;

import org.example.dto.ApiException;
import org.example.dto.ReportsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private ReportsDto reportsDto;

    @Scheduled(fixedDelay = 50000)
    public void generateDayReport() throws ApiException {
reportsDto.generateDayReports();
    }
}
