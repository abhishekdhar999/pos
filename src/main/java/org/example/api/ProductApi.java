package org.example.api;

import org.example.dao.ProductDao;
import org.example.dto.ApiException;
import org.example.models.ProductData;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductApi {

   @Autowired
   private ProductDao  productDao;

//    public void uploadProducts(List<ProductPojo> products) throws ApiException {
//
//    productDao.addProduct(products);
//    }

//    public static List<ProductPojo> getProducts(){
//        return productDao.getAllProducts();
//    }

    public void createProduct(ProductPojo pojo) throws ApiException {
        productDao.createProduct(pojo);
    }
}
