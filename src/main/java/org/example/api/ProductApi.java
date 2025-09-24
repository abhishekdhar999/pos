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
        return productDao.getAll(page, size, keyword);
    }

    public void update(Integer id, ProductPojo productPojo) throws ApiException {
        ProductPojo existingProduct = getById(id);
     if (Objects.isNull(existingProduct)){
            throw new ApiException("Product with id '"+id+"' doesn't exists");
        } else if (!existingProduct.getBarcode().equals(productPojo.getBarcode())) {
            checkDuplicateBarcode(productPojo.getBarcode());
        }
     existingProduct.setPrice(productPojo.getPrice());
     existingProduct.setName(productPojo.getName());
     existingProduct.setImageUrl(productPojo.getImageUrl());
    }

    public ProductPojo getByBarcode(String barcode)throws ApiException{
        return productDao.getByBarcode(barcode);
    }

    public ProductPojo getById(Integer id) throws ApiException{
        return productDao.getById(id);
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
