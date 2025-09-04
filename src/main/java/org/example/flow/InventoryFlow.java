package org.example.flow;

import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.pojo.ClientPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;


@Service
@Transactional
public class InventoryFlow {
    @Autowired
    private ProductApi productApi;

    public ProductPojo getProductByBarcode(String barcode) throws ApiException{
        ProductPojo productPojo = productApi.getByBarcode(barcode);
        if(Objects.isNull(productPojo)){
            throw new ApiException("Barcode doesn't exists.");
        }
        return productPojo;
    }

    public ProductPojo getProductByProductId(Integer productId) throws ApiException {
        return productApi.getById(productId);
    }
}
