package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.InventoryDto;
import org.example.models.data.InventoryData;
import org.example.models.form.InventoryForm;
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

//    in update inventory i need the id of the inventory
       @ApiOperation(value = "creating inventory ")
        @RequestMapping(value = "/update-inventory/{id}")
    public void updateInventory(@PathVariable int id,@RequestBody InventoryForm inventoryForm) throws ApiException {
inventoryDto.updateInventoryById(id,inventoryForm);
        }


//        in crerte invemtory i need the id of the product
        @ApiOperation(value = "create inventory")
        @RequestMapping(value = "/create-inventory/{id}")
        public void createInventory(@PathVariable int id,@RequestBody InventoryForm form) throws ApiException {
           inventoryDto.createInventory(id,form);
        }

        @ApiOperation(value = "get inventory by productid")
    @RequestMapping(value = "/inventory/{id}")
    public InventoryData getInventoryByProductId(@PathVariable int id) throws ApiException {
return inventoryDto.getInventoryByProductId(id);
        }

//        @ApiOperation(value = "get inventory by product id")
//    @RequestMapping(value = "/")



}
