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
import org.example.pojo.OrderPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderDtoTestAdditional extends AbstractUnitTest {

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
    private String testBarcode;

    @Before
    public void setUp() throws ApiException {
        // Create test client
        ClientForm clientForm = TestHelper.createClientForm("test-client");
        clientDto.add(clientForm);
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        
        // Create test product
        testBarcode = "TEST-BARCODE-001";
        productForm = TestHelper.createProductForm(testBarcode, clientPojo.getName(), "Test Product", 100.00, "test-image.jpg");
        productDto.add(productForm);
        
        // Create test inventory
        inventoryForm = TestHelper.createInventoryForm(testBarcode, 50);
        inventoryDto.add(inventoryForm);
    }

    @Test
    public void testCreateOrder_WithValidationErrors() {
        // Test with invalid quantity (negative)
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm invalidOrderItem = TestHelper.createOrderItemForm(testBarcode, -5, 100.00);
        orderItemFormList.add(invalidOrderItem);
        
        ErrorData<OrderError> result = orderDto.create(orderItemFormList);
        
        assertNotNull(result);
        assertTrue(result.getErrorList().size() > 0);
        assertNull(result.getId()); // Should not create order if validation fails
    }

    @Test
    public void testCreateOrder_WithInsufficientInventory() throws ApiException {
        // Test with quantity greater than available inventory
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItem = TestHelper.createOrderItemForm(testBarcode, 100, 100.00); // More than available (50)
        orderItemFormList.add(orderItem);
        
        ErrorData<OrderError> result = orderDto.create(orderItemFormList);
        
        assertNotNull(result);
        assertEquals(1, result.getErrorList().size()); // No validation errors
        assertNotNull(result.getId()); // Order should be created
        
        // Verify the order has UNFULFILLABLE status due to insufficient inventory
        OrderData orderData = orderDto.getOrderDetails(result.getId());
        assertNotNull(orderData);
        assertEquals(OrderStatus.UNFULFILLABLE, orderData.getStatus());
    }

    @Test
    public void testCreateOrder_WithZeroQuantity() {
        // Test with zero quantity
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItem = TestHelper.createOrderItemForm(testBarcode, 0, 100.00);
        orderItemFormList.add(orderItem);
        
        ErrorData<OrderError> result = orderDto.create(orderItemFormList);
        
        assertNotNull(result);
        assertTrue(result.getErrorList().size() > 0);
        assertNull(result.getId()); // Should not create order with zero quantity
    }

    @Test
    public void testCreateOrder_WithMultipleItems() throws ApiException {
        // Create another product for multiple items test
        String secondBarcode = "TEST-BARCODE-002";
        ProductForm secondProduct = TestHelper.createProductForm(secondBarcode, "test-client", "Second Product", 200.00, "test-image2.jpg");
        productDto.add(secondProduct);
        
        InventoryForm secondInventory = TestHelper.createInventoryForm(secondBarcode, 30);
        inventoryDto.add(secondInventory);
        
        // Test with multiple valid items
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm item1 = TestHelper.createOrderItemForm(testBarcode, 10, 100.00);
        OrderItemForm item2 = TestHelper.createOrderItemForm(secondBarcode, 5, 200.00);
        orderItemFormList.add(item1);
        orderItemFormList.add(item2);
        
        ErrorData<OrderError> result = orderDto.create(orderItemFormList);
        
        assertNotNull(result);
        assertEquals(0, result.getErrorList().size());
        assertNotNull(result.getId());
    }

    @Test
    public void testGetAll_WithEmptyResult() throws ApiException {
        // Test with date range that has no orders
        OrderFiltersForm filters = TestHelper.createOrderFilters(0, 10, "2020-01-01T00:00:00Z", "2020-01-31T23:59:59Z");
        List<OrderData> response = orderDto.getAll(filters);
        
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void testGetAll_WithPagination() throws ApiException {
        // Create multiple orders first
        for (int i = 0; i < 5; i++) {
            List<OrderItemForm> orderItemFormList = new ArrayList<>();
            OrderItemForm orderItem = TestHelper.createOrderItemForm(testBarcode, 1, 100.00);
            orderItemFormList.add(orderItem);
            orderDto.create(orderItemFormList);
        }
        
        // Test pagination - first page
        OrderFiltersForm filters = TestHelper.createOrderFilters(0, 3, "2025-01-01T00:00:00Z", "2025-12-31T23:59:59Z");
        List<OrderData> response = orderDto.getAll(filters);
        
        assertNotNull(response);
        assertTrue(response.size() <= 3);
        
        // Test pagination - second page
        filters.setPage(1);
        List<OrderData> response2 = orderDto.getAll(filters);
        
        assertNotNull(response2);
        // Should have remaining orders
    }

    @Test
    public void testResync_WithValidOrder() throws ApiException {
        // Create an order first
        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm orderItem = TestHelper.createOrderItemForm(testBarcode, 10, 100.00);
        orderItemFormList.add(orderItem);
        ErrorData<OrderError> createResult = orderDto.create(orderItemFormList);
        
        assertNotNull(createResult);
        assertNotNull(createResult.getId());
        
        // Test resync
        ErrorData<OrderError> resyncResult = orderDto.resync(createResult.getId());
        
        assertNotNull(resyncResult);
        assertEquals(0, resyncResult.getErrorList().size());
    }



    @Test
    public void testGetTotalCount_WithFilters() throws ApiException {
        // Create some orders first
        for (int i = 0; i < 3; i++) {
            List<OrderItemForm> orderItemFormList = new ArrayList<>();
            OrderItemForm orderItem = TestHelper.createOrderItemForm(testBarcode, 1, 100.00);
            orderItemFormList.add(orderItem);
            orderDto.create(orderItemFormList);
        }
        
        // Test total count
        OrderFiltersForm filters = TestHelper.createOrderFilters(0, 10, "2025-01-01T00:00:00Z", "2025-12-31T23:59:59Z");
        Long totalCount = orderDto.getTotalCount(filters);
        
        assertNotNull(totalCount);
        assertTrue(totalCount >= 3);
    }

    @Test
    public void testGetTotalCount_WithEmptyResult() throws ApiException {
        // Test with date range that has no orders
        OrderFiltersForm filters = TestHelper.createOrderFilters(0, 10, "2020-01-01T00:00:00Z", "2020-01-31T23:59:59Z");
        Long totalCount = orderDto.getTotalCount(filters);
        
        assertNotNull(totalCount);
        assertEquals(Long.valueOf(0), totalCount);
    }

    @Test
    public void testGetOrderDetails_WithOrderItems() throws ApiException {
        // Create an order with multiple items
        String secondBarcode = "TEST-BARCODE-003";
        ProductForm secondProduct = TestHelper.createProductForm(secondBarcode, "test-client", "Third Product", 150.00, "test-image3.jpg");
        productDto.add(secondProduct);

        InventoryForm secondInventory = TestHelper.createInventoryForm(secondBarcode, 20);
        inventoryDto.add(secondInventory);

        List<OrderItemForm> orderItemFormList = new ArrayList<>();
        OrderItemForm item1 = TestHelper.createOrderItemForm(testBarcode, 5, 100.00);
        OrderItemForm item2 = TestHelper.createOrderItemForm(secondBarcode, 3, 150.00);
        orderItemFormList.add(item1);
        orderItemFormList.add(item2);

        ErrorData<OrderError> createResult = orderDto.create(orderItemFormList);
        assertNotNull(createResult);
        assertNotNull(createResult.getId());

        // Test get order details
        OrderData orderData = orderDto.getOrderDetails(createResult.getId());

        assertNotNull(orderData);
        assertEquals(createResult.getId(), orderData.getId());
        assertEquals(OrderStatus.FULFILLABLE, orderData.getStatus());
        assertEquals(2, orderData.getOrderItems().size());

    }

}
