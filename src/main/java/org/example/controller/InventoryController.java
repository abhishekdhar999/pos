package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.InventoryDto;
import org.example.models.InventoryData;
import org.example.models.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api")
public class InventoryController {

    @Autowired
    InventoryDto inventoryDto;

       @ApiOperation(value = "creating inventory ")
        @RequestMapping(value = "/update-inventory/{id}")
    public void createInventory(@PathVariable int id,@RequestBody InventoryForm inventoryForm) throws ApiException {
inventoryDto.updateInventoryById(id,inventoryForm);
        }

        @ApiOperation(value = "get inventory by productid")
    @RequestMapping(value = "/inventory/{id}")
    public InventoryData getInventoryById(@PathVariable int id) throws ApiException {
return inventoryDto.getInventoryById(id);
        }



}
