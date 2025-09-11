package org.example.dto;

import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.flow.InventoryFlow;
import org.example.flow.ProductFlow;
import org.example.models.data.InventoryData;
import org.example.models.data.Response;
import org.example.models.form.InventoryForm;
import org.example.pojo.InventoryPojo;
import org.example.utils.BulkResponse;
import org.example.utils.BulkUploadResult;
import org.example.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component

public class InventoryDto {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private InventoryFlow inventoryFlow;
    @Autowired
    private ProductFlow productFlow;
    @Autowired
    private ProductApi productApi;

    public void add(InventoryForm inventoryForm) throws ApiException {
        UtilMethods.normalizeInventoryForm(inventoryForm);
        UtilMethods.validateInventoryForm(inventoryForm);
        InventoryPojo inventoryPojo = convert(inventoryForm);
        inventoryApi.add(inventoryPojo);
    }


    public List<Response<InventoryForm>> bulkUpload(List<InventoryForm> listOfInventoryForm) throws ApiException {

        List<Response<InventoryForm>> responseList = new ArrayList<>();


        for(InventoryForm inventoryForm: listOfInventoryForm){
            Response<InventoryForm> response = new Response<>();
            response.setData(inventoryForm);
            response.setMessage("success");
            try{
                add(inventoryForm);
            }catch (ApiException e){
                response.setMessage(e.getMessage());
            }
            responseList.add(response);
        }
        return responseList;
    }

    public List<InventoryData> getAll() throws ApiException{
        List<InventoryPojo> inventoryPojoList = inventoryApi.getAll();
        List<InventoryData> inventoryDataList = new ArrayList<>();
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            inventoryDataList.add(convert(inventoryPojo));
        }
        return inventoryDataList;
    }

    public void edit(InventoryForm inventoryForm) throws ApiException {
        UtilMethods.normalizeInventoryForm(inventoryForm);
        UtilMethods.validateInventoryForm(inventoryForm);
        InventoryPojo inventoryPojo = convert(inventoryForm);
        inventoryApi.edit(inventoryPojo);
    }

    public InventoryData getByProductId(Integer productId) throws ApiException{
        InventoryPojo inventoryPojo = inventoryApi.getByProductId(productId);
        if(Objects.isNull(inventoryPojo)){
            throw new ApiException("Out of stock");
        }
        return convert(inventoryPojo);
    }

    public InventoryData getByBarcode(String barcode) throws ApiException {
        Integer productId = inventoryFlow.getProductByBarcode(barcode).getId();
        return getByProductId(productId);
    }


    private InventoryPojo convert(InventoryForm inventoryForm) throws ApiException{
        Integer productId = inventoryFlow.getProductByBarcode(inventoryForm.getBarcode()).getId();

        return DtoHelper.convertInventoryFormToInventoryPojo(inventoryForm, productId);
    }

    private InventoryData convert(InventoryPojo inventoryPojo) throws ApiException{
        String barcode = inventoryFlow.getProductByProductId(inventoryPojo.getProductId()).getBarcode();
        return DtoHelper.convertInventoryPojoToInventoryData(inventoryPojo, barcode);
    }

}
