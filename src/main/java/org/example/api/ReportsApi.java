package org.example.api;

import org.example.dto.ApiException;
import org.example.models.form.DaySalesReportsForm;
import org.example.pojo.DaySalesReportPojo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ReportsApi {
//    public List<DaySalesReportPojo> getDaysSalesReports(DaySalesReportsForm form) throws ApiException{
//
//    }
}
