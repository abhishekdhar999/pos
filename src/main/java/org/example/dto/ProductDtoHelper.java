package org.example.dto;

import io.swagger.models.auth.In;
import org.example.api.ClientApi;
import org.example.api.InventoryApi;
import org.example.models.ProductData;
import org.example.models.ProductForm;
import org.example.pojo.ClientPojo;
import org.example.pojo.InventoryPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Service
public class ProductDtoHelper {


//    @Autowired
//    private static InventoryApi inventoryApi;
//    @Autowired
//    private static ClientApi clientApi;

//    public static boolean checkClientIsValid(int clientId) throws ApiException {
//      ClientPojo client =   clientApi.getClientById(clientId);
//        return client != null;
//    }

//    file.getInputStream()
//    This method is called on a MultipartFile (typically uploaded via a form).
//
//    It returns an InputStream — a stream of raw bytes representing the content of the uploaded file.
    public static List<ProductData> parseTsv(MultipartFile file) throws ApiException, IOException {

        List<ProductData> products = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            String line;
            boolean headerSkipped = false;

            while((line = reader.readLine()) != null){
                if (!headerSkipped) {
                    headerSkipped = true;  // skip first header line
                    continue;
                }

                String[] tokens = line.split("\t");
                if (tokens.length < 4) continue;

                String name = tokens[0];
                double price = Double.parseDouble(tokens[1]);
                String description = tokens[2];
                int clientId = Integer.parseInt(tokens[3]);

//                if(!checkClientIsValid(clientId)){
//                    System.out.println("Skipping: Client ID " + clientId + " does not exist.");
//                    continue;
//                }

                ProductData product = new ProductData();
                product.setName(name);
                product.setPrice(price);

                product.setClientId(clientId);  // If you're using manual clientId

                products.add(product);

            }
        }

        return products;
    }

    public static List<ProductPojo> convertToProductPojo(List<ProductData> products) {
        List<ProductPojo> productPojos = new ArrayList<>();
        for (ProductData product : products) {
            ProductPojo productPojo = new ProductPojo();
            productPojo.setName(product.getName());
            productPojo.setBarcode(product.getBarcode());
            productPojo.setPrice(product.getPrice());
            productPojo.setClientId(product.getClientId());
            productPojo.setImageUrl(product.getImageUrl());
            productPojo.setSku(product.getSku());
            productPojo.setCategory(product.getCategory());
            productPojos.add(productPojo);

        }

        return productPojos;
    }

//    public static  List<ProductData> convertToProductData(List<ProductPojo> products) throws ApiException {
//        List<ProductData> productDatas = new ArrayList<>();
//        for (ProductPojo product : products) {
//            InventoryPojo inventoryPojo = inventoryApi.getInventoryById(product.getProductId());
//
//            ProductData productData = new ProductData();
//
//            productData.setName(product.getName());
//            productData.setBarcode(product.getBarcode());
//            productData.setPrice(product.getPrice());
//            productData.setClientId(product.getClientId());
//            productData.setImageUrl(product.getImageUrl());
//            productData.setSku(product.getSku());
//            productData.setCategory(product.getCategory());
//
//            productData.setQuantity(inventoryPojo.getQuantity());
//            productDatas.add(productData);
//
//        }
//        return productDatas;
//    }

public static ProductPojo convertProductFormToProductPojo (ProductForm form){
        ProductPojo product = new ProductPojo();
        product.setName(form.getName());
        product.setBarcode(form.getBarcode());
        product.setPrice(form.getPrice());
        product.setImageUrl(form.getImageUrl());
        product.setSku(form.getSku());
        product.setCategory(form.getCategory());
        return product;

}
}
