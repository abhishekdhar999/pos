package org.example.dto;

import org.example.api.InventoryApi;
import org.example.models.InventoryData;
import org.example.models.InventoryForm;
import org.example.models.ProductData;
import org.example.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import static org.example.dto.InventoryDtoHelper.convertInventoryPojoToInventoryData;

@Service
public class InventoryDto {

    @Autowired
    InventoryApi inventoryApi;

    public InventoryData getInventoryById(int id) throws ApiException{

        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo = inventoryApi.getInventoryById(id);

   InventoryData  inventoryData = convertInventoryPojoToInventoryData(inventoryPojo);
        return inventoryData;
    }

    public void updateInventoryById(int id ,InventoryForm form) throws ApiException{
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo = inventoryApi.getInventoryById(id);


    }
}
