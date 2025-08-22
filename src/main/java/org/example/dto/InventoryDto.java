package org.example.dto;

import org.example.api.InventoryApi;
import org.example.flow.InventoryFlow;
import org.example.flow.ProductFlow;
import org.example.models.data.InventoryData;
import org.example.models.form.InventoryForm;
import org.example.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.example.dto.InventoryDtoHelper.*;

@Service
public class InventoryDto {

    @Autowired
    InventoryApi inventoryApi;

    @Autowired
    ProductFlow productFlow;
    @Autowired
    private InventoryFlow inventoryFlow;

    public InventoryData getInventoryByProductId(int id) throws ApiException{

        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo = inventoryApi.getInventoryByProductId(id);

   InventoryData  inventoryData = convertInventoryPojoToInventoryData(inventoryPojo);
        return inventoryData;
    }

    public void updateInventoryById(int id ,InventoryForm form) throws ApiException{
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo = inventoryApi.getInventoryByProductId(id);


    }
//need product id
    public void createInventory(int id,InventoryForm form) throws ApiException{

       InventoryPojo inventoryPojo = convertInventoryFormToInventoryPojo(form,id);
        inventoryApi.createInventory(inventoryPojo);

    }

//    public ProducctPojo convert(int id,InventoryForm form) throws ApiException {
//       inventoryFlow.getProductByIdInInventoryFlow(id);
//      convertFormToPojo(form,id);
//    }
}
