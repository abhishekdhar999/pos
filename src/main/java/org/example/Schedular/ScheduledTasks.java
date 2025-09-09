package org.example.Schedular;

import org.example.dto.ReportsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private ReportsDto reportsDto;

    @Scheduled(cron = "0 00 12 * * ?")
    public void generateDayReport() {
reportsDto.generateDayReports();
    }
}
