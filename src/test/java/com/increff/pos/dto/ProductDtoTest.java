package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.dao.ClientDao;
import org.example.dao.ProductDao;
import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.dto.ProductDto;
import org.example.models.data.ProductData;
import org.example.models.data.Response;
import org.example.models.form.ClientForm;
import org.example.models.form.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto productDto;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ClientDto clientDto;
    @Autowired
    private ProductDao productDao;

    private ClientForm clientForm ;

    @Before
    public void setup() throws ApiException {
        clientForm  = TestHelper.createClientForm("amazon");
        clientDto.add(clientForm);
    }

@Test
public void testCreateProduct() throws ApiException {
    ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
    // valid product
    ProductForm validProductForm = TestHelper.createProductForm("barcode_valid", clientPojo.getName(), "demo_product", 200.0, "image.jpg");
    productDto.add(validProductForm);
    ProductPojo savedProduct = productDao.getByBarcode(validProductForm.getBarcode());
    assertNotNull(savedProduct);
    assertEquals(validProductForm.getBarcode(), savedProduct.getBarcode());
    assertEquals(validProductForm.getName(), savedProduct.getName());
    assertEquals(validProductForm.getPrice(), savedProduct.getPrice(), 0.001);
    assertEquals(validProductForm.getImageUrl(), savedProduct.getImageUrl());
//    empty barcode
    ProductForm emptyBarcodeForm = TestHelper.createProductForm("", clientPojo.getName(), "product_no_barcode", 100.0, "image.jpg");
    assertThrows(ApiException.class, () -> productDto.add(emptyBarcodeForm));
    ProductForm emptyClientForm = TestHelper.createProductForm("barcode_empty_client", "", "product_empty_client", 100.0, "image.jpg");
    assertThrows(ApiException.class, () -> productDto.add(emptyClientForm));
//  empty product name
    ProductForm emptyNameForm = TestHelper.createProductForm("barcode_empty_name", clientPojo.getName(), "", 100.0, "image.jpg");
    assertThrows(ApiException.class, () -> productDto.add(emptyNameForm));
    ProductForm spacesNameForm = TestHelper.createProductForm("barcode_spaces_name", clientPojo.getName(), "   ", 100.0, "image.jpg");
    assertThrows(ApiException.class, () -> productDto.add(spacesNameForm));
//  negative price
    ProductForm negativePriceForm = TestHelper.createProductForm("barcode_negative_price", clientPojo.getName(), "product_negative_price", -50.0, "image.jpg");
    assertThrows(ApiException.class, () -> productDto.add(negativePriceForm));
}

    @Test
    public void testUpdateProduct() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm = TestHelper.createProductForm("barcode123", clientPojo.getName(), "demo_product", 200.0, "image.jpg");
        productDto.add(productForm);
        ProductPojo savedProduct = productDao.getByBarcode(productForm.getBarcode());
        assertNotNull(savedProduct);
        ProductForm updateForm = TestHelper.createProductForm("barcode123", clientPojo.getName(), "demo_product_updated", 400.0, "updated_image.jpg");
        productDto.update(savedProduct.getId(), updateForm);
        ProductPojo updatedProduct = productDao.getByBarcode(productForm.getBarcode());
        assertNotNull(updatedProduct);
        assertEquals(updateForm.getName(), updatedProduct.getName());
        assertEquals(updateForm.getPrice(), updatedProduct.getPrice(), 0.001);
        assertEquals(updateForm.getImageUrl(), updatedProduct.getImageUrl());
        //  invalid price
        ProductForm invalidPriceForm = TestHelper.createProductForm("barcode123", clientPojo.getName(), "demo_product_invalid", -50.0, "image.jpg");
        assertThrows(ApiException.class, () -> productDto.update(savedProduct.getId(), invalidPriceForm));
        //  empty name
        ProductForm emptyNameForm = TestHelper.createProductForm("barcode123", clientPojo.getName(), "", 300.0, "image.jpg");
        assertThrows(ApiException.class, () -> productDto.update(savedProduct.getId(), emptyNameForm));
        //  empty client
        ProductForm emptyClientForm = TestHelper.createProductForm("barcode123", "", "demo_product", 300.0, "image.jpg");
        assertThrows(ApiException.class, () -> productDto.update(savedProduct.getId(), emptyClientForm));
    }


    @Test
    public void testGetProductById() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"demo_product",200.0,"");
        productDto.add(productForm);
        ProductPojo savedProduct = productDao.getByBarcode(productForm.getBarcode());
        ProductData productData = productDto.getById(savedProduct.getId());
        assertNotNull(productData);
        assertEquals(savedProduct.getBarcode(), productData.getBarcode());
    }

    @Test
    public void testGetAll() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"demo_product",200.0,"");
        productDto.add(productForm);
        List<ProductData> productData = productDto.getAll(0,1,"");
        assertNotNull(productData);
        assertEquals(1,productData.size());
        assertEquals(productForm.getBarcode(), productData.get(0).getBarcode());
    }

    @Test
    public void testSearchByBarcode() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm1 = TestHelper.createProductForm("barcode_1",clientPojo.getName(),"demo_product_1",400.0,"");
        productDto.add(productForm1);
        ProductForm productForm2 = TestHelper.createProductForm("barcode_2",clientPojo.getName(),"demo_product_2",200.0,"");
        productDto.add(productForm2);

        List<String> productData  = productDto.searchByBarcode(0,10,"barcode");

        assertNotNull(productData);
        assertEquals(2, productData.size());
        assertTrue(productData.contains("barcode_1"));
        assertTrue(productData.contains("barcode_2"));
    }

    @Test
    public void testBulkUploadWithValidation() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());

        List<ProductForm> productFormList = new ArrayList<>();
        productFormList.add(TestHelper.createProductForm("", clientPojo.getName(), "valid_name", 100.0, "img.jpg"));
        productFormList.add(TestHelper.createProductForm("barcode2", clientPojo.getName(), "", 200.0, "img.jpg"));
        productFormList.add(TestHelper.createProductForm("barcode3", clientPojo.getName(), "valid_name_2", -10.0, "img.jpg"));
        productFormList.add(TestHelper.createProductForm("barcode4", clientPojo.getName(), "valid_name_3", 300.0, "img.jpg"));
        List<Response<ProductForm>> responses = productDto.bulkUpload(productFormList);
        assertNotNull(responses);
        assertEquals(4, responses.size());

        assertNotEquals("success", responses.get(0).getMessage());
        assertNotEquals("success", responses.get(1).getMessage());
        assertNotEquals("success", responses.get(2).getMessage());
        assertEquals("success", responses.get(3).getMessage());
    }
    @Test
    public void testBulkUploadWithoutClient() throws ApiException {
       List<ProductForm> productFormList = new ArrayList<>();
       productFormList.add(TestHelper.createProductForm("barcode","non_existing_client","valid_name",200.00,"img.jpg"));
       List<Response<ProductForm>> responses = productDto.bulkUpload(productFormList);
       assertNotNull(responses);
       assertEquals(1, responses.size());
        assertNotEquals("success", responses.get(0).getMessage());
    }


}
