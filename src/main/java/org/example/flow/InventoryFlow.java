package org.example.flow;

import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.pojo.ClientPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
public class InventoryFlow {
    @Autowired
    private ClientApi clientApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private InventoryApi inventoryApi;

    public ClientPojo getClientByIdInInventoryFlow(int id) throws ApiException {
       return clientApi.getClientById(id);
    }

//    public ProductPojo getProductByIdInInventoryFlow(int id) throws ApiException {
//
//    }
}
