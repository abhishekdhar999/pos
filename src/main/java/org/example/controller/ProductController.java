package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.ProductDto;
import org.example.models.data.OperationResponse;
import org.example.models.data.ProductData;
import org.example.models.form.ProductForm;
import org.example.pojo.ProductPojo;
import org.example.utils.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
@RestController
@RequestMapping(value = "/api/product")
public class ProductController {

    @Autowired
    private ProductDto productDto;


    @ApiOperation("add a single product")
    @RequestMapping(method = RequestMethod.POST)
    public void add(@RequestBody ProductForm productForm) throws ApiException {
        productDto.add(productForm);
    }

    @ApiOperation("get all client's info")
    @RequestMapping(method = RequestMethod.GET)
    public PaginatedResponse<ProductData> getAll(@RequestParam Integer page, @RequestParam Integer size, @RequestParam(defaultValue = "") String keyword) throws ApiException{
        List<ProductData> data =  productDto.getAll(page, size, keyword);
        PaginatedResponse<ProductData> response = new PaginatedResponse<>();
        response.setData(data);
        response.setPage(page);
        response.setSize(size);

        return response;
    }

    @ApiOperation("Update a product's details")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id, @RequestBody ProductForm productForm) throws ApiException{
        productDto.update(id, productForm);
    }

//    @ApiOperation("Add multiple products using tsv.")
//    @RequestMapping(path = "/upload", method = RequestMethod.POST)
//    public List<OperationResponse<ProductForm>> bulkUpload(@RequestBody List<ProductForm> productFormList) throws ApiException{
//        return productDto.batchAdd(productFormList);
//    }

    @ApiOperation("Add multiple products using tsv.")
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public List<OperationResponse<ProductForm>> Upload (@RequestParam("file") MultipartFile file) throws ApiException{
        if (file.isEmpty()) {
            throw new ApiException("File is empty");
        }
        return productDto.batchAdd(file);

    }



    @ApiOperation("get by id")
    @RequestMapping(path = "/id/{id}", method = RequestMethod.GET)
    public ProductData getById(@PathVariable Integer id) throws ApiException{
        return productDto.getById(id);
    }

    @ApiOperation("Get by barcode")
    @RequestMapping(path = "/barcode/{barcode}", method = RequestMethod.GET)
    public ProductData getByBarcode(@PathVariable String barcode) throws ApiException{
        return productDto.getByBarcode(barcode);
    }

    @ApiOperation("getting total no. of products")
    @RequestMapping(path = "/count", method = RequestMethod.GET)
    public Long getTotalCount(){
        return productDto.getTotalCount();
    }

    @ApiOperation("search by barcode")
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public List<String> searchByBarcode(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String barcode){
        return productDto.searchByBarcode(page, size, barcode);
    }
}
