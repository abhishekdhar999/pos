package org.example.api;

import org.example.dao.InventoryDao;
import org.example.dto.ApiException;
import org.example.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class InventoryApi {

    @Autowired
    private  InventoryDao inventoryDao;

    public  InventoryPojo getInventoryById(int id) throws ApiException {
       return inventoryDao.getInventoryById(id);
    }

}
