package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.dao.ClientDao;
import org.example.dao.ProductDao;
import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.dto.ProductDto;
import org.example.models.data.ProductData;
import org.example.models.form.ClientForm;
import org.example.models.form.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.ProductPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        ProductForm productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"demo_product",200.0,"");
        productDto.add(productForm);
        ProductPojo pojo = productDao.getByBarcode(productForm.getBarcode());
        assertNotNull(pojo);  // product exists
        assertEquals(productForm.getBarcode(), pojo.getBarcode());
        assertEquals(productForm.getBarcode(), pojo.getBarcode());
        assertEquals(productForm.getName(), pojo.getName());
        assertEquals(productForm.getPrice(), pojo.getPrice());
        productForm.setPrice(-90.00);
        assertThrows(ApiException.class, () -> productDto.add(productForm));
    }
    @Test
    public void testAddProductWithEmptyBarcode() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());

        // Create a product form with empty barcode
        ProductForm emptyBarcodeForm = TestHelper.createProductForm(
                "",                     // empty barcode
                clientPojo.getName(),
                "product_no_barcode",
                100.0,
                "image.jpg"
        );

        // Expect ApiException because barcode is invalid
        assertThrows(ApiException.class, () -> productDto.add(emptyBarcodeForm));
    }

    @Test
    public void testAddProductWithEmptyClient() throws ApiException {

        // Empty client
        ProductForm emptyClientForm = TestHelper.createProductForm(
                "barcode_empty_client",
                "",
                "product_empty_client",
                100.0,
                "image.jpg"
        );
        assertThrows(ApiException.class, () -> productDto.add(emptyClientForm));

        // Client with spaces only
        ProductForm spacesClientForm = TestHelper.createProductForm(
                "barcode_spaces_client",
                "   ",
                "product_spaces_client",
                100.0,
                "image.jpg"
        );
        assertThrows(ApiException.class, () -> productDto.add(spacesClientForm));
    }


    @Test
    public void testCreateProductWithNegativePrice() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"demo_product",-200.0,"");

    }

    @Test
    public void testUpdateProduct() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"demo_product",200.0,"");
        productDto.add(productForm);
        ProductPojo savedProduct = productDao.getByBarcode(productForm.getBarcode());
        ProductForm updateProductForm = TestHelper.createProductForm("barcode",clientPojo.getName(),"demo_product_updated",400.00,"");
        productDto.update(savedProduct.getId(), updateProductForm);
        ProductPojo updatedProductPojo = productDao.getByBarcode(productForm.getBarcode());
        assertNotNull(updatedProductPojo);
        assertEquals(updateProductForm.getPrice(), updatedProductPojo.getPrice(), 0.001);
        assertEquals(updateProductForm.getName(), updatedProductPojo.getName());
        assertEquals(updateProductForm.getImageUrl(), updatedProductPojo.getImageUrl());
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
        assertEquals(productForm.getBarcode(), productData.get(0).getBarcode());
    }

    @Test
    public void testSearchByBarcode() throws ApiException {
        ClientPojo clientPojo = clientDao.getByName(clientForm.getName());
        ProductForm productForm = TestHelper.createProductForm("barcode_1",clientPojo.getName(),"demo_product_1",400.0,"");
        productDto.add(productForm);
        ProductForm productForm2 = TestHelper.createProductForm("barcode_2",clientPojo.getName(),"demo_product_2",200.0,"");
        productDto.add(productForm2);



    }
}
