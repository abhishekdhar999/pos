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
        InventoryPojo inventory = inventoryDao.getByProductId(inventoryPojo.getProductId());
        if(Objects.isNull(inventory)){
            inventoryDao.add(inventoryPojo);
        }else{
            inventory.setQuantity(inventory.getQuantity() + inventoryPojo.getQuantity());
        }
    }

    public void bulkUpload(List<InventoryPojo> inventoryPojoList) {
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            InventoryPojo inventory = inventoryDao.getByProductId(inventoryPojo.getProductId());
            if(Objects.isNull(inventory)){
                inventoryDao.add(inventoryPojo);
            }else{
                inventory.setQuantity(inventory.getQuantity() + inventoryPojo.getQuantity());
//                inventoryDao.update(inventoryPojo);
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

//    public void edit(InventoryPojo inventoryPojo) {
//        InventoryPojo inventory = inventoryDao.getByProductId(inventoryPojo.getProductId());
//        if(Objects.isNull(inventory)){
//            inventoryDao.add(inventoryPojo);
//        }else{
//            inventoryPojo.setId(inventory.getId());
//            inventoryDao.update(inventoryPojo);
//        }
//    }
}
