package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.dao.ClientDao;
import org.example.dao.InventoryDao;
import org.example.dao.ProductDao;
import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.dto.InventoryDto;
import org.example.dto.ProductDto;
import org.example.models.data.InventoryData;
import org.example.models.data.Response;
import org.example.models.form.ClientForm;
import org.example.models.form.InventoryForm;
import org.example.models.form.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InventoryDtoTest extends AbstractUnitTest {

@Autowired
private ClientDto clientDto;
@Autowired
private ClientDao  clientDao;
@Autowired
private InventoryDto inventoryDto;
@Autowired
private InventoryDao inventoryDao;
@Autowired
private ProductDao productDao;
@Autowired
private ProductDto productDto;

private ProductForm productForm;
    @Before
    public void setUp() throws ApiException {
      ClientForm  clientForm = TestHelper.createClientForm("amazon");
        clientDto.add(clientForm);
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"product_dummy",200.00,"image.jpg");
        productDto.add(productForm);
    }

    @Test
    public void testCreateInventory() throws ApiException {
        InventoryForm inventoryForm = TestHelper.createInventoryForm(productForm.getBarcode(), 100);
        inventoryDto.add(inventoryForm);
        ProductPojo productPojo = productDao.getByBarcode(productForm.getBarcode());
        assertNotNull(productPojo);
        InventoryPojo inventoryPojo = inventoryDao.getByProductId(productPojo.getId());
        assertNotNull(inventoryPojo);
        assertEquals(inventoryPojo.getProductId(),productPojo.getId());
//        negative price
        InventoryForm negativeFormInventoryForm = TestHelper.createInventoryForm(productForm.getBarcode(), -100);
        assertThrows(ApiException.class, () -> inventoryDto.add(negativeFormInventoryForm));
//        invalid barcode
        InventoryForm invalidBarcodeInventoryForm = TestHelper.createInventoryForm("invalid_barcode",inventoryForm.getQuantity());
        assertThrows(ApiException.class, () -> inventoryDto.add(invalidBarcodeInventoryForm));
    }

    @Test
    public void testInventoryBulkUpload() throws ApiException {
        List<InventoryForm> inventoryFormList = new ArrayList<>();
        inventoryFormList.add(TestHelper.createInventoryForm("barcode",100));
        inventoryFormList.add(TestHelper.createInventoryForm("invalid_barcode",100));
        inventoryFormList.add(TestHelper.createInventoryForm("valid_barcode",-100));
        List<Response<InventoryForm>> responses = inventoryDto.bulkUpload(inventoryFormList);
        assertNotNull(responses);
        assertEquals(3, responses.size());

        assertEquals("success",responses.get(0).getMessage());
        assertNotEquals("success",responses.get(1).getMessage());
        assertNotEquals("success",responses.get(2).getMessage());
    }

    @Test
    public void testGetAll() throws ApiException {
    InventoryForm inventoryForm1 = TestHelper.createInventoryForm(productForm.getBarcode(),100);
    InventoryForm inventoryForm2 = TestHelper.createInventoryForm(productForm.getBarcode(),100);
    inventoryDto.add(inventoryForm1);
    inventoryDto.add(inventoryForm2);
    List<InventoryData> inventoryDataList = inventoryDto.getAll();
    assertNotEquals(2,inventoryDataList.size());
    }

    @Test
    public void testGetInventoryByBarcode() throws ApiException {
        InventoryForm inventoryForm = TestHelper.createInventoryForm(productForm.getBarcode(),100);
        inventoryDto.add(inventoryForm);
        InventoryData inventoryData = inventoryDto.getByBarcode(productForm.getBarcode());
        assertNotNull(inventoryData);
        assertEquals(inventoryForm.getBarcode(), inventoryData.getBarcode());
        assertEquals(inventoryForm.getQuantity(), inventoryData.getQuantity());
    }






}
