package org.example.flow;

import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.dto.ProductDto;
import org.example.dto.ProductDtoHelper;
import org.example.models.ClientForm;
import org.example.models.ProductData;
import org.example.models.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.dto.ProductDtoHelper.*;

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
        return clientApi.getClientByName(name);
    }

    public void createProduct(ProductPojo product) throws ApiException {
        productApi.createProduct(product);
    }

    public void create(ProductForm form) throws ApiException {
        ClientPojo clientPojo = clientApi.getClientByName(form.getClientName());
       ProductPojo product =  productDto.convert(form,clientPojo);
        productApi.createProduct(product);
    }


}
