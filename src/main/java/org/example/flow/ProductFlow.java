package org.example.flow;

import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.models.data.Response;
import org.example.pojo.ClientPojo;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.util.*;

@Service
public class ProductFlow {

    @Autowired
    private ClientApi clientApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;

    public void add(ProductPojo productPojo) throws ApiException {
        checkIfBarcodeAlreadyExist(productPojo.getBarcode());
        doesClientExists(productPojo.getClientId());
        productApi.add(productPojo);
    }

    public void update(Integer id, ProductPojo productPojo) throws ApiException{
        doesClientExists(productPojo.getClientId());
        productApi.update(id, productPojo);
    }

    public ClientPojo getClientById(Integer clientId) throws ApiException {
        return clientApi.getById(clientId);
    }

    public ClientPojo getClientByName(String name) throws ApiException{
        ClientPojo clientPojo = clientApi.getByName(name);
        if(Objects.isNull(clientPojo)){
            throw new ApiException("Client "+name+" doesn't exists.");
        }
        return clientPojo;
    }

//    todo save only integer
    public InventoryPojo getInventoryByProductId(Integer productId) throws ApiException {
        ProductPojo productPojo = productApi.getById(productId);
        return inventoryApi.getByProductId(productId);
    }

    private void doesClientExists(Integer clientId) throws ApiException{
        try{
            clientApi.getById(clientId);
        }catch (ApiException e){
            throw new ApiException("Client doesn't exists");
        }
    }
    public void checkIfBarcodeAlreadyExist(String barcode) throws ApiException{
        try{
            productApi.getByBarcode(barcode);
        }catch (ApiException e){
            throw new ApiException("Barcode doesn't exists");
        }
    }


}
