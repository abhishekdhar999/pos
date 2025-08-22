package org.example.dto;

import org.example.api.ProductApi;
import org.example.flow.ProductFlow;
import org.example.models.data.ProductData;
import org.example.models.form.ProductForm;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.example.dto.ProductDtoHelper.*;

@Service
public class ProductDto {
    @Autowired
    private ProductFlow productFlow;



//    public   List<ProductData> getProductData() throws ApiException {
//        List<ProductData> productsData = new ArrayList<>();
//
//        List<ProductPojo> productPojos = productApi.getProducts();
//
//        productsData = convertToProductData(productPojos);
//        return productsData;
//
//    }

    public void createProduct(ProductForm form) throws ApiException {
     ProductPojo pojo = convert(form);
        productFlow.createProduct(pojo);
    }

    public ProductPojo  convert(ProductForm form) throws ApiException {
      Integer ClientId = productFlow.gettingClientIdByName(form.getClientName());

        return convertProductFormToProductPojo(form,ClientId);
    }

    public ProductData getProductById(int id) throws ApiException {

        InventoryPojo inventoryPojo =  productFlow.getInventoryByProductId(id);
      ProductPojo productPojo =   productFlow.getProductById(id);

      return  convertPojoToData(productPojo,inventoryPojo);
    }

}
