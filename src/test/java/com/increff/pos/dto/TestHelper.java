package com.increff.pos.dto;

import com.sun.istack.NotNull;
import org.example.models.form.ClientForm;
import org.example.models.form.ProductForm;
import org.example.pojo.ClientPojo;

public class TestHelper {

    public static ClientForm createClientForm(String name){
        ClientForm form = new ClientForm();
        form.setName(name);
        return form;
    }
    public static ClientForm createUpdateClientForm( String name){
        ClientForm updateForm = new ClientForm();
        updateForm.setName(name);
        return updateForm;
    }
public static ClientPojo CreateClientPojo(String name){
        ClientPojo clientPojo = new ClientPojo();
        clientPojo.setName(name);
        return clientPojo;
}
//    product test dto helper
    public static ProductForm createProductForm(String barcode,String clientName,String name,Double price,String imageUrl){
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(barcode);
        productForm.setClientName(clientName);
        productForm.setName(name);
        productForm.setPrice(price);
        productForm.setImageUrl(imageUrl);
        return productForm;

    }
}
