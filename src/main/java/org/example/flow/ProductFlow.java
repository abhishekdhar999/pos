package org.example.flow;

import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.dto.ProductDto;
import org.example.models.data.OperationResponse;
import org.example.pojo.ClientPojo;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ProductFlow {

    @Autowired
    private ClientApi clientApi;
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;

    public void add(ProductPojo productPojo) throws ApiException {
        doesClientExists(productPojo.getClientId());
        productApi.add(productPojo);
    }

    public void update(Integer id, ProductPojo productPojo) throws ApiException{
        doesClientExists(productPojo.getClientId());
        productApi.update(id, productPojo);
    }

    public List<OperationResponse<ProductPojo>> batchAdd(List<ProductPojo> productPojoList) {
        List<OperationResponse<ProductPojo>> operationResponseList = new ArrayList<>();

        Set<String> barcodes = new HashSet<>();
        boolean errorOccured = false;
        for(ProductPojo productPojo: productPojoList){
            OperationResponse<ProductPojo> operationResponse = new OperationResponse<>();
            operationResponse.setData(productPojo);
            operationResponse.setMessage("No error");
            try{
                if(barcodes.contains(productPojo.getBarcode())){
                    throw new ApiException("File contain duplicate barcodes");
                } else {
                    barcodes.add(productPojo.getBarcode());
                }
                doesClientExists(productPojo.getClientId());
            } catch (ApiException e){
                errorOccured = true;
                operationResponse.setMessage(e.getMessage());
            }
            operationResponseList.add(operationResponse);
        }

        if(errorOccured){
            return operationResponseList;
        }
        return productApi.batchAdd(productPojoList);
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


}
