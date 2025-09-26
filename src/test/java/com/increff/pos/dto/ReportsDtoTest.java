package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.dao.ClientDao;
import org.example.dao.SalesReportDao;
import org.example.dto.*;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.SalesReportData;
import org.example.models.form.*;
import org.example.pojo.ClientPojo;
import static org.junit.Assert.*;

import org.example.pojo.DaySalesReportPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportsDtoTest extends AbstractUnitTest {

    @Autowired
    private ClientDto clientDto;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private OrderDto orderDto;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ReportsDto reportsDto;
@Autowired
private SalesReportDao salesReportDao;

    private ClientForm clientForm;
    private ProductForm productForm;
    private InventoryForm inventoryForm;
    private OrderItemForm orderItemForm;

    @Before
    public void setUp() throws Exception {
        ClientForm clientForm = TestHelper.createClientForm("amazon");
        clientDto.add(clientForm);
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"product_dummy",200.00,"image.jpg");
        productDto.add(productForm);
        inventoryForm = TestHelper.createInventoryForm(productForm.getBarcode(),100);
        inventoryDto.add(inventoryForm);
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItemForm1 = TestHelper.createOrderItemForm(productForm.getBarcode(),20,200.00);
        orderItemFormList.add(orderItemForm1);
        orderDto.create(orderItemFormList);
    }

    @Test
    public void testGatDailySalesReport() throws Exception {
        ZonedDateTime dateTime = ZonedDateTime.now();
        DaySalesReportPojo pojo = TestHelper.createDailySalesReportPojo(dateTime, 10, 10, 1000.0);
        salesReportDao.addDaySalesReport(pojo);
       DaySalesReportsForm form = TestHelper.createDaySalesReportForm(0, 10,dateTime.minusDays(1).toString(), dateTime.plusDays(1).toString());
        List<DaySalesReportData> result = reportsDto.getDaysSalesReports(form);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getInvoicedOrdersCount().intValue());
    }

    @Test
    public void testGetTotalSales() throws Exception {
        SalesReportFilterForm salesReportFilterForm = TestHelper.createSalesReportFilterForm(0,10,"2025-08-01T00:00:00Z", "2025-10-31T23:59:59Z","","");
       List<SalesReportData> salesReportData =  reportsDto.getSalesReport(salesReportFilterForm);
       assertNotNull(salesReportData);
       assertEquals(1,salesReportData.size());
       assertEquals(salesReportData.get(0).getProductBarcode(),productForm.getBarcode());
    }

    @Test
    public void testEmptyGetDailySalesReport() throws ApiException {
        DaySalesReportsForm form = TestHelper.createDaySalesReportForm(
                ZonedDateTime.now().minusDays(10).toString(),
                ZonedDateTime.now().minusDays(5).toString(),
                0,
                10
        );
        List<DaySalesReportData> result = reportsDto.getDaysSalesReports(form);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
