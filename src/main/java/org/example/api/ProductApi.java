package org.example.api;

import org.example.dao.ProductDao;
import org.example.dto.ApiException;
import org.example.models.data.Response;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional()
public class ProductApi {

    @Autowired
    private ProductDao productDao;

    public void add(ProductPojo productPojo) throws ApiException{
        checkDuplicateBarcode(productPojo.getBarcode());
        productDao.add(productPojo);
    }

    public List<ProductPojo> getAll(Integer page, Integer size, String keyword) throws ApiException {
        Long totalCount = productDao.getTotalCount();
        if(totalCount!=0 && (long) page *size >= totalCount){
            throw new ApiException("Invalid page number");
        }
        return productDao.getAll(page, size, keyword);
    }

    public void update(Integer id, ProductPojo productPojo) throws ApiException {

        //checking if product with given id exists or not, if exists then checking for duplicate barcode, only if it is getting updated.
        ProductPojo existingProduct = getById(id);
     if (Objects.isNull(existingProduct)){
            throw new ApiException("Product with id '"+id+"' doesn't exists");
        } else if (!existingProduct.getBarcode().equals(productPojo.getBarcode())) {
            checkDuplicateBarcode(productPojo.getBarcode());
        }
        productDao.update(id, productPojo);
    }

//    public List<Response<ProductPojo>> batchAdd(List<ProductPojo> productPojoList){
//        List<Response<ProductPojo>> responseList = new ArrayList<>();
//
//        boolean errorOccured = false;
//        for(ProductPojo productPojo: productPojoList){
//            Response<ProductPojo> response = new Response<>();
//            response.setData(productPojo);
//            response.setMessage("No error");
//            try{
//                checkDuplicateBarcode(productPojo.getBarcode());
//            }catch (ApiException e){
//                response.setMessage(e.getMessage());
//                errorOccured = true;
//            }
//            responseList.add(response);
//        }
//        if(!errorOccured){
//            productDao.batchAdd(productPojoList);
//        }
//        return responseList;
//    }

    public ProductPojo getByBarcode(String barcode)throws ApiException{
        return productDao.getByBarcode(barcode);
    }

    public ProductPojo getById(Integer id) throws ApiException{
        return productDao.getById(id);
    }

    public Long getTotalCount(){
        return productDao.getTotalCount();
    }


    private void checkDuplicateBarcode(String barcode) throws ApiException{
        ProductPojo productPojo = productDao.getByBarcode(barcode);
        if(Objects.nonNull(productPojo)){
            throw new ApiException("Barcode should be unique. Product '"+productPojo.getName()+"' already has barcode: '"+ barcode+"'");
        }
    }


    public List<String> searchByBarcode(Integer page, Integer size, String barcode) {
        return productDao.searchByBarcode(page, size, barcode);
    }
}
