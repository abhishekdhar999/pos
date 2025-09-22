package org.example.dto;

import org.example.api.ProductApi;
import org.example.flow.ProductFlow;
import org.example.models.data.Response;
import org.example.models.data.ProductData;
import org.example.models.form.ProductForm;
import org.example.pojo.ProductPojo;
import org.example.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Service
public class ProductDto {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private ProductFlow productFlow;

    public void add(ProductForm productForm) throws ApiException{
        UtilMethods.normalizeProductForm(productForm);
        UtilMethods.validateProductForm(productForm);
        ProductPojo productPojo = convert(productForm);
        productFlow.add(productPojo);
    }

    public List<ProductData> getAll(Integer page, Integer size, String keyword) throws ApiException{
        List<ProductPojo> productPojoList = productApi.getAll(page, size, keyword);

        List<ProductData> productDataList = new ArrayList<>();
        for(ProductPojo productPojo: productPojoList){
            ProductData productData = convert(productPojo);
            productDataList.add(productData);
        }
        return productDataList;
    }

    public void update(Integer id, ProductForm productForm) throws ApiException{
        UtilMethods.normalizeProductForm(productForm);
        UtilMethods.validateProductForm(productForm);
        ProductPojo productPojo = convert(productForm);
        productFlow.update(id, productPojo);
    }

    public List<Response<ProductForm>> bulkUpload(List<ProductForm> productFormList) throws ApiException {
        List<Response<ProductForm>> responseList = new ArrayList<>();
        for(ProductForm productForm: productFormList){
            Response<ProductForm> response = new Response<>();
            response.setData(productForm);
            response.setMessage("success");
            try{
                add(productForm);
            } catch (ApiException e){
                response.setMessage(e.getMessage());
            } catch (Exception e){
                response.setMessage("Error: " + e.getMessage());
            }
            responseList.add(response);
        }
        return responseList;
    }

    public ProductData getById(Integer id) throws ApiException{
        ProductPojo productPojo = productApi.getById(id);
        return convert(productPojo);
    }
    public ProductData getByBarcode(String barcode) throws ApiException{
        ProductPojo productPojo = productApi.getByBarcode(barcode);
        if(Objects.isNull(productPojo)){
            throw new ApiException("Product doesn't exists with barcode '"+barcode+"'. ");
        }
        return convert(productPojo);
    }
    public List<String> searchByBarcode(Integer page, Integer size, String barcode) {
        return productApi.searchByBarcode(page, size, barcode);
    }
    private ProductData convert(ProductPojo productPojo) throws ApiException{
        String clientName = productFlow.getClientById(productPojo.getClientId()).getName();
        Integer inventory = productFlow.getInventoryByProductId(productPojo.getId()).getQuantity();
        return DtoHelper.convertProductPojoToProductData(productPojo, clientName, inventory);
    }
    private ProductPojo convert(ProductForm productForm) throws ApiException{
        Integer clientId = productFlow.getClientByName(productForm.getClientName()).getId();
        return DtoHelper.convertProductFormToProductPojo(productForm, clientId);
    }
}
