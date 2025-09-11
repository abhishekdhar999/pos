package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaySalesReportData {
    private String date;
    private Integer invoicedOrdersCount;
    private Integer invoicedItemsCount;
    private Double totalRevenue;
}
