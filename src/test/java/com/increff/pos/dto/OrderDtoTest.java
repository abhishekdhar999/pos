package com.increff.pos.dto;

import org.example.dao.ClientDao;
import org.example.dao.InventoryDao;
import org.example.dao.ProductDao;
import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.dto.InventoryDto;
import org.example.dto.ProductDto;
import org.example.models.form.ClientForm;
import org.example.models.form.InventoryForm;
import org.example.models.form.OrderFiltersForm;
import org.example.models.form.ProductForm;
import org.example.pojo.ClientPojo;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderDtoTest {

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
    public void testCreateOrder(){
       OrderFiltersForm orderForm = TestHelper.createOrderForm(){

        }

}
