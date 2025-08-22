package org.example.flow;

import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.dto.ProductDto;
import org.example.dto.ProductDtoHelper;
import org.example.models.form.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ProductFlow {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private  ClientApi clientApi;

    @Autowired
    private ProductDto  productDto;
    @Autowired
    private ProductDtoHelper productDtoHelper;

    public  Integer gettingClientIdByName(String name) throws ApiException {
        ClientPojo pojo =  clientApi.getClientByName(name);
        return pojo.getId();
    }

    public void createProduct(ProductPojo productPojo) throws ApiException {
        productApi.createProduct(productPojo);
    }

    public InventoryPojo getInventoryByProductId(int id) throws ApiException {
        return inventoryApi.getInventoryByProductId(id);
    }

    public ProductPojo getProductById(int id) throws ApiException {


        return productApi.getProductById(id);
    }
//    public ProductPojo getProduct(int id) throws ApiException {}
//    public void create(ProductPojo pojo) throws ApiException {
//        productApi.createProduct(pojo);
//    }


}
