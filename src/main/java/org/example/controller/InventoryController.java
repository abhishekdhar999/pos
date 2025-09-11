package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.InventoryDto;
import org.example.models.data.InventoryData;
import org.example.models.data.Response;
import org.example.models.form.InventoryForm;
import org.example.utils.BulkUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

//import static com.sun.nio.zipfs.ZipFileAttributeView.AttrID.method;

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
    @ApiOperation("bulk uploading")
    @RequestMapping(path = "/bulk",method = RequestMethod.POST)
    public List<Response<InventoryForm>> bulk(@RequestBody List<InventoryForm> listOfInventoryForm) throws ApiException{

        return inventoryDto.bulkUpload(listOfInventoryForm);
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
    @ApiOperation("download the current inventory")
    @RequestMapping(path = "/download",method = RequestMethod.GET)
    public void downloadInventory(HttpServletResponse response) throws ApiException, IOException {
        List<InventoryData> listOfInventoryData = inventoryDto.getAll();
if(Objects.isNull(listOfInventoryData)){
    throw new ApiException("error fetching the inventory data");
}
        response.setContentType("text/tab-separated-values");
        response.setHeader("Content-Disposition", "attachment; filename=inventory.tsv");

//        write tsv data
        PrintWriter writer = response.getWriter();
        writer.println("barcode\tquantity");

        for(InventoryData inventoryData : listOfInventoryData){
            writer.println(inventoryData.getBarcode()+"\t"+inventoryData.getQuantity());
        }
        writer.flush();
    }

}
