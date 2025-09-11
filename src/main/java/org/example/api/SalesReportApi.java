package org.example.api;

import org.example.dao.SalesReportDao;
import org.example.dto.ApiException;
import org.example.models.data.DaySalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.pojo.DaySalesReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SalesReportApi {

    @Autowired
    private SalesReportDao salesReportDao;
    public void addDaySalesReport(DaySalesReportPojo daySalesReportPojo)throws ApiException{
        salesReportDao.addDaySalesReport(daySalesReportPojo);
    }

    public List<DaySalesReportPojo> getDaySalesReports(DaySalesReportsForm daySalesReportsForm) throws ApiException{
        ZonedDateTime startDate = ZonedDateTime.parse(daySalesReportsForm.getStartDate());
        ZonedDateTime endDate = ZonedDateTime.parse(daySalesReportsForm.getEndDate());
      return  salesReportDao.getDaySalesReports(startDate,endDate,daySalesReportsForm.getPage(),daySalesReportsForm.getSize());
    }
    public Long getTotalDayReports(){
        return salesReportDao.getTotalDayReports();
    }
    public List<DaySalesReportPojo> getDaySalesReportsBetweenDates(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException{
        return salesReportDao.getDaySalesReportsBetweenDates(startDate,endDate);
    }
}
