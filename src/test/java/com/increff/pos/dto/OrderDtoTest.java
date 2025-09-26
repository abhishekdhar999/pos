package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.dao.ClientDao;
import org.example.dao.InventoryDao;
import org.example.dao.OrderDao;
import org.example.dao.ProductDao;
import org.example.dto.*;
import org.example.enums.OrderStatus;
import org.example.models.data.ErrorData;
import org.example.models.data.OrderData;
import org.example.models.data.OrderError;
import org.example.models.form.*;
import org.example.pojo.ClientPojo;
import org.example.pojo.OrderItemPojo;
import org.example.pojo.OrderPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private ClientDto clientDto;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductDto productDto;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDto orderDto;

    private ProductForm productForm;
    private InventoryForm inventoryForm;

    @Before
    public void setUp() throws ApiException {
        ClientForm clientForm = TestHelper.createClientForm("amazon");
        clientDto.add(clientForm);
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"product_dummy",200.00,"image.jpg");
        productDto.add(productForm);
        inventoryForm = TestHelper.createInventoryForm(productForm.getBarcode(),100);
        inventoryDto.add(inventoryForm);
    }

    @Test
    public void testCreateOrder(){
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
       OrderItemForm orderItemForm = TestHelper.createOrderItemForm(productForm.getBarcode(),20,200.00);
        orderItemFormList.add(orderItemForm);
        ErrorData<OrderError> result =  orderDto.create(orderItemFormList);
        assertNotNull(result);
        assertEquals(0,result.getErrorList().size());
        }

    @Test
    public void testCreateWithInvalidBarcode() {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItemForm = TestHelper.createOrderItemForm("invalid-barcode", 10, 80.0);
        orderItemFormList.add(orderItemForm);
        ErrorData<OrderError> result = orderDto.create(orderItemFormList);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(1, result.getErrorList().size());
    }
    @Test
    public void testGetOrderDetails() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItemForm = TestHelper.createOrderItemForm(productForm.getBarcode(),20,200.00);
        orderItemFormList.add(orderItemForm);
        ErrorData<OrderError> result =  orderDto.create(orderItemFormList);
        OrderData orderData = orderDto.getOrderDetails(result.getId());
        assertNotNull(orderData);
        assertEquals(orderData.getId(), result.getId());
        assertEquals(OrderStatus.CREATED, orderData.getStatus());
        assertEquals(1, orderData.getOrderItems().size());
    }

    @Test
    public void testGetAll() throws ApiException {
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItemForm = TestHelper.createOrderItemForm(productForm.getBarcode(),20,200.00);
        orderItemFormList.add(orderItemForm);
        ErrorData<OrderError> result =  orderDto.create(orderItemFormList);
        OrderFiltersForm filters = TestHelper.createOrderFilters(0, 10, "2025-08-01T00:00:00Z", "2025-10-31T23:59:59Z");
        List<OrderData> response = orderDto.getAll(filters);
        assertNotNull(response);
        assertEquals(1, response.size());
    }


}
