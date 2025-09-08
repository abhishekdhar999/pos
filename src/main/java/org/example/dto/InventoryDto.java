package org.example.dto;

import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.flow.InventoryFlow;
import org.example.flow.ProductFlow;
import org.example.models.data.InventoryData;
import org.example.models.data.OperationResponse;
import org.example.models.form.InventoryForm;
import org.example.models.form.ProductForm;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.example.utils.BulkResponse;
import org.example.utils.BulkUploadResult;
import org.example.utils.InventoryUploadResult;
import org.example.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.utils.UtilMethods.convertFileToInventoryFormList;


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

    public BulkUploadResult<InventoryForm> bulk(MultipartFile file) throws ApiException {
        InventoryUploadResult result = convertFileToInventoryFormList(file);

        BulkUploadResult<InventoryForm> finalResult = new BulkUploadResult<>();
        List<BulkResponse<InventoryForm>> failures = new ArrayList<>();
        int successCount = 0;
        int idx = 0;

        // Handle parsing-level errors
        for (String error : result.getErrors()) {
            BulkResponse<InventoryForm> failure = new BulkResponse<>();
            failure.setIndex(idx++);
            failure.setSuccess(false);
            failure.setMessage(error);
            failure.setData(null);
            failures.add(failure);
        }

        // Row-level processing
        for (InventoryForm inventoryForm : result.getInventories()) {
            try {
                add(inventoryForm); // your DB save method
                successCount++;
            } catch (Exception e) {
                BulkResponse<InventoryForm> failure = new BulkResponse<>();
                failure.setIndex(idx++);
                failure.setSuccess(false);
                failure.setMessage("Error processing " + inventoryForm.getBarcode() + ": " + e.getMessage());
                failure.setData(inventoryForm);
                failures.add(failure);
            }
        }

        finalResult.setSuccessCount(successCount);
        finalResult.setFailureCount(failures.size());
        finalResult.setFailures(failures);

        return finalResult;
    }


    public List<OperationResponse<InventoryForm>> bulkUpload(MultipartFile file) throws ApiException {

        List<OperationResponse<InventoryForm>> operationResponseListt = new ArrayList<>();
       InventoryUploadResult inventoryUploadResult   = convertFileToInventoryFormList(file);
       if(inventoryUploadResult.getErrors() != null){
           for(String error : inventoryUploadResult.getErrors()){
               int idx = inventoryUploadResult.getErrors().indexOf(error);
               OperationResponse<InventoryForm> operationResponse = new OperationResponse<>();
               operationResponse.setData(inventoryUploadResult.getInventories().get(idx));
               operationResponse.setMessage(error);
               operationResponseListt.add(operationResponse);
           }
          return  operationResponseListt;
       }
            List<InventoryForm> inventoryFormList = inventoryUploadResult.getInventories();
       for(InventoryForm inventoryForm : inventoryFormList){
           System.out.println(inventoryForm.getBarcode());
           System.out.println(inventoryForm.getQuantity());
       }
        for (InventoryForm inventoryForm : inventoryFormList) {
            System.out.println("Barcode: " + inventoryForm.getBarcode());

            System.out.println("Price: " + inventoryForm.getQuantity());

        }

        List<InventoryPojo> inventoryPojoList = new ArrayList<>();
        List<OperationResponse<InventoryForm>> operationResponseList = new ArrayList<>();

        boolean errorOccured = false;
        for(InventoryForm inventoryForm: inventoryFormList){
            OperationResponse<InventoryForm> operationResponse = new OperationResponse<>();
            operationResponse.setData(inventoryForm);
            operationResponse.setMessage("No error");
            try{
                UtilMethods.normalizeInventoryForm(inventoryForm);
                UtilMethods.validateInventoryForm(inventoryForm);
                InventoryPojo inventoryPojo = convert(inventoryForm);
                inventoryPojoList.add(inventoryPojo);
            }catch (ApiException e){
                operationResponse.setMessage(e.getMessage());
                errorOccured = true;
            }
            operationResponseList.add(operationResponse);
        }
        if(!errorOccured){
            inventoryApi.bulkUpload(inventoryPojoList);
        }
        return operationResponseList;
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
