package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.ProductDto;
import org.example.models.data.ProductData;
import org.example.models.form.ProductForm;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping(value = "/api")
public class ProductController {

    @Autowired
    ProductDto productDto;


//    @ApiOperation(value = "uplaod projects via tsv file")
//    @RequestMapping(value = "/upload-products", method = RequestMethod.POST)
//    public void uploadProducts(@RequestParam MultipartFile file) throws ApiException, IOException {
//
//productDto.operationOnTsvFile(file);
//    }

//    @ApiOperation(value = "get all products")
//    @RequestMapping(value = "/products", method = RequestMethod.GET)
//    public List<ProductData> getAllProducts() throws ApiException {
//      return  productDto.getProductData();
//    }


    @ApiOperation(value = "creating a single product")
    @RequestMapping(value = "/product",method = RequestMethod.POST)
    public void createProduct(@RequestBody ProductForm form) throws ApiException {
        productDto.createProduct(form);
    }

    @ApiOperation(value = "get product by id")
    @RequestMapping(value = "/product/{id}",method = RequestMethod.GET)
    public ProductData getProductById(@PathVariable int id) throws ApiException {
       return productDto.getProductById(id);
    }
}
