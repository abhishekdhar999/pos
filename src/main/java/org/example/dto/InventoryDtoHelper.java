package org.example.dto;

import org.example.models.InventoryData;
import org.example.models.InventoryForm;
import org.example.models.ProductData;
import org.example.pojo.InventoryPojo;

public class InventoryDtoHelper {



      public static InventoryData convertInventoryPojoToInventoryData(InventoryPojo pojo){
            InventoryData inventoryData = new InventoryData();
            inventoryData.setId(pojo.getId());
            inventoryData.setProductId(pojo.getProductId());
            inventoryData.setQuantity(pojo.getQuantity());
            return inventoryData;

    }

    public static InventoryPojo convertFormToPojo(InventoryForm form){
          InventoryPojo inventoryPojo = new InventoryPojo();
          inventoryPojo.setProductId(form.getProductId());
          inventoryPojo.setQuantity(form.getQuantity());
          return inventoryPojo;
    }
}
