package org.example.dto;

import org.example.api.ClientApi;
import org.example.api.ProductApi;
import org.example.flow.ProductFlow;
import org.example.models.ClientData;
import org.example.models.ProductData;
import org.example.models.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.dto.ProductDtoHelper.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
//       ClientPojo clientpojo = productFlow.gettingClientIdByName(form.getClientName());
//        ProductPojo product = convertProductFormToProductPojo(form,clientpojo);
//
//        productFlow.createProduct(product);



     ProductPojo pojo = convert(form);
        productFlow.create(pojo);
    }

    public ProductPojo  convert(ProductForm form) throws ApiException {
      Integer ClientId = productFlow.gettingClientIdByName(form.getClientName());
        ProductPojo pojo = convertProductFormToProductPojo(form,ClientId);

        return convertProductFormToProductPojo(form,);
    }

//    public void creatProduct(ProductForm form) throws ApiException {
//        productFlow.create(form);
//    }


}
