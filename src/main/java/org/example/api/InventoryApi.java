package org.example.api;

import org.example.dao.InventoryDao;
import org.example.dto.ApiException;
import org.example.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class InventoryApi {

    @Autowired
    private InventoryDao inventoryDao;

    public void add(InventoryPojo inventoryPojo){
        // checking if inventory already exists
        InventoryPojo inventory = inventoryDao.getByProductId(inventoryPojo.getProductId());

        //if it doesn't exist then we will create a new row, otherwise update the existing inventory
        if(Objects.isNull(inventory)){
            inventoryDao.add(inventoryPojo);
        }else{
            //increasing inventory
            inventoryPojo.setQuantity(inventory.getQuantity() + inventoryPojo.getQuantity());
            inventoryDao.update(inventoryPojo);
        }
    }

    public void bulkUpload(List<InventoryPojo> inventoryPojoList) {
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            // checking if inventory already exists
            InventoryPojo inventory = inventoryDao.getByProductId(inventoryPojo.getProductId());
            //if it doesn't exist then we will create a new row, otherwise update the existing inventory
            if(Objects.isNull(inventory)){
                inventoryDao.add(inventoryPojo);
            }else{
                //increasing inventory
                inventoryPojo.setQuantity(inventory.getQuantity() + inventoryPojo.getQuantity());
                inventoryDao.update(inventoryPojo);
            }
        }
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.getAll();
    }

    public InventoryPojo getByProductId(Integer productId){
        InventoryPojo inventoryPojo = inventoryDao.getByProductId(productId);
        if(Objects.isNull(inventoryPojo)){
            inventoryPojo = new InventoryPojo();
            inventoryPojo.setProductId(productId);
            inventoryPojo.setQuantity(0);
        }
        return inventoryPojo;
    }

    public void edit(InventoryPojo inventoryPojo) {
        // checking if inventory already exists
        InventoryPojo inventory = inventoryDao.getByProductId(inventoryPojo.getProductId());

        //if it doesn't exist then we will create a new row, otherwise update the existing inventory
        if(Objects.isNull(inventory)){
            inventoryDao.add(inventoryPojo);
        }else{
            // Set the ID from existing inventory to ensure proper update
            inventoryPojo.setId(inventory.getId());
            inventoryDao.update(inventoryPojo);
        }
    }
}
