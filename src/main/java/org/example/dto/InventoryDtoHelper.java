package org.example.dto;

import org.example.models.data.InventoryData;
import org.example.models.form.InventoryForm;
import org.example.pojo.InventoryPojo;

public class InventoryDtoHelper {



      public static InventoryData convertInventoryPojoToInventoryData(InventoryPojo pojo){
            InventoryData inventoryData = new InventoryData();
            inventoryData.setId(pojo.getId());
            inventoryData.setProductId(pojo.getProductId());
            inventoryData.setQuantity(pojo.getQuantity());
            return inventoryData;

    }

    public static InventoryPojo convertFormToPojo(InventoryForm form,int id) throws ApiException{
          InventoryPojo inventoryPojo = new InventoryPojo();
          inventoryPojo.setId(id);
          inventoryPojo.setQuantity(form.getQuantity());
          return inventoryPojo;
    }

    public static InventoryPojo convertInventoryFormToInventoryPojo(InventoryForm form,int id) throws ApiException{
          InventoryPojo inventoryPojo = new InventoryPojo();
          inventoryPojo.setQuantity(form.getQuantity());
          inventoryPojo.setProductId(id);

          return inventoryPojo;
    }
}
