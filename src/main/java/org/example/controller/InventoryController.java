package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.InventoryDto;
import org.example.models.data.InventoryData;
import org.example.models.data.OperationResponse;
import org.example.models.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto inventoryDto;

    @ApiOperation("add inventory of a product")
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public void add(@RequestBody InventoryForm inventoryForm) throws ApiException {
        inventoryDto.add(inventoryForm);
    }

    @ApiOperation("add inventories of mulitple products")
    @RequestMapping(path = "/upload", method = RequestMethod.PUT)
    public List<OperationResponse<InventoryForm>> bulkUpload(@RequestParam("file") MultipartFile file) throws ApiException{
        if(file.isEmpty()){
            throw new ApiException("file is empty");
        }
        return inventoryDto.bulkUpload(file);
    }

    @ApiOperation("edit inventory of a product")
    @RequestMapping(method = RequestMethod.PUT)
    public void edit(@RequestBody InventoryForm inventoryForm) throws ApiException{
        inventoryDto.edit(inventoryForm);
    }

    @ApiOperation("get all inventories")
    @RequestMapping(method = RequestMethod.GET)
    public List<InventoryData> getAll() throws ApiException{
        return inventoryDto.getAll();
    }

    @ApiOperation("get inventory by product id")
    @RequestMapping(path = "/get-by-product-id/{productId}", method = RequestMethod.GET)
    public InventoryData getByProductId(@PathVariable Integer productId) throws ApiException{
        return inventoryDto.getByProductId(productId);
    }

    @ApiOperation("get inventory by product id")
    @RequestMapping(path = "/get-by-barcode/{barcode}", method = RequestMethod.GET)
    public InventoryData getByBarcode(@PathVariable String barcode) throws ApiException{
        return inventoryDto.getByBarcode(barcode);
    }

}
