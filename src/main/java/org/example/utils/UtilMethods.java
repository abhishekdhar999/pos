package org.example.utils;

import lombok.Getter;
import lombok.Setter;
import org.example.dto.ApiException;
import org.example.models.data.OrderError;
import org.example.models.form.ClientForm;
import org.example.models.form.InventoryForm;
import org.example.models.form.OrderItemForm;
import org.example.models.form.ProductForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.*;

@Getter
@Setter
public class UtilMethods {
    public static List<ProductForm> convertFileInToProductFormList(MultipartFile file) throws ApiException {
        List<ProductForm> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true; // skip header if any
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header
                }
                String[] columns = line.split("\t"); // split by tab
                if (columns.length < 4) continue; // adjust based on required fields

                ProductForm product = new ProductForm();
                product.setName(columns[0]);
                product.setBarcode(columns[1]);
                product.setClientName(columns[2]);
                product.setImageUrl(columns[3]);
                product.setPrice(Double.parseDouble(columns[4]));
                products.add(product);
            }

        } catch (Exception e) {
            throw new ApiException("Failed to process TSV file: " + e.getMessage());
        }
return products;
        // Call your service to save products in bulk

    }

    public static void normalizeClientForm(ClientForm clientForm){
        clientForm.setName(clientForm.getName().toLowerCase().trim());
    }
//todo 5 should be in constants
    public static void validateClientForm(ClientForm clientForm) throws ApiException {
        if(Objects.isNull(clientForm) || Objects.isNull(clientForm.getName())) {
            throw new ApiException("Client should not be null");
        }else if (clientForm.getName().isEmpty()) {
            throw new ApiException("Client name should not be empty");
        } else if (clientForm.getName().length() > FinalValues.MAXIMUM_LENGTH) {
            throw new ApiException("Client name should not exceed "+ FinalValues.MAXIMUM_LENGTH +" letters");
        }else if(clientForm.getName().length()< 5){
            throw new ApiException("Client name should be less than 5 letters");
        }
    }

    public static void validateProductForm(ProductForm productForm) throws ApiException{
        if(Objects.isNull(productForm)){
            throw new ApiException("Product should not be null");
        }else if(Objects.isNull(productForm.getPrice())){
            throw new ApiException("Product price should not be empty");
        }else if(productForm.getName().isEmpty()){
            throw new ApiException("Product name should not be empty");
        }else if(productForm.getBarcode().isEmpty()){
            throw new ApiException("Barcode should not be empty");
        }else if (productForm.getBarcode().length() > FinalValues.MAXIMUM_LENGTH) {
            throw new ApiException("Barcode should not exceed "+ FinalValues.MAXIMUM_LENGTH +" letters");
        }else if (productForm.getName().length() > FinalValues.MAXIMUM_LENGTH) {
            throw new ApiException("Product name should not exceed "+ FinalValues.MAXIMUM_LENGTH +" letters");
        } else if (productForm.getImageUrl().length() > FinalValues.URL_LENGTH) {
            throw new ApiException("Barcode should not exceed "+ FinalValues.URL_LENGTH +" letters");
        } else if (productForm.getPrice() <= 0.0) {
        throw new ApiException("Product price should be greater than 0");
        } else if (productForm.getPrice() > FinalValues.FINAL_PRICE) {
            throw new ApiException("Mrp should not exceed ₹"+ FinalValues.FINAL_PRICE);
        }else if(productForm.getBarcode().length() < 5){
            throw new ApiException("Barcode should be more than 5 letters");
        }else if(productForm.getName().length() < 5){
            throw new ApiException("Product name should be more than 5 letters");
        }
    }

    public static void normalizeProductForm(ProductForm productForm) throws ApiException {
        if(Objects.isNull(productForm.getPrice())){
            throw new ApiException("Product price should not be empty");
        }
        productForm.setPrice( Double.valueOf(new DecimalFormat("0.00").format( productForm.getPrice() )) );
        productForm.setName(productForm.getName().trim().toLowerCase());
        productForm.setBarcode(productForm.getBarcode().trim().toLowerCase());
        productForm.setImageUrl(productForm.getImageUrl().trim());
        productForm.setClientName(productForm.getClientName().trim().toLowerCase());
    }

    public static void validateInventoryForm(InventoryForm inventoryForm) throws ApiException{
        if(Objects.isNull(inventoryForm.getBarcode())){
            throw new ApiException("Barcode should not be empty");
        }else if(Objects.isNull(inventoryForm.getQuantity())){
            throw new ApiException("Quantity should not be empty");
        }else if(inventoryForm.getQuantity() <= 0){
            throw new ApiException("Quantity should be greater than 0");
        } else if (inventoryForm.getQuantity() > FinalValues.FINAL_INVENTORY) {
            throw new ApiException("Quantity should not exceed "+ FinalValues.FINAL_INVENTORY);
        } else if(inventoryForm.getBarcode().length() > FinalValues.MAXIMUM_LENGTH){
            throw new ApiException("Barcode should not be greater then "+ FinalValues.MAXIMUM_LENGTH +" letters");
        }else if(inventoryForm.getBarcode().isEmpty()){
            throw new ApiException("Barcode should not be empty");
        }else if(inventoryForm.getQuantity().toString().isEmpty()){
            throw new ApiException("Quantity should not be empty");
        }
    }

    public static void normalizeOrderForm(OrderItemForm orderItemForm){
        orderItemForm.setBarcode(orderItemForm.getBarcode().trim().toLowerCase());
    }

    public static void validateOrderForm(OrderItemForm orderItemForm) throws ApiException {
        if(orderItemForm.getQuantity() <= 0){
            throw new ApiException("Quantity should be greater than 0.");
        } else if (orderItemForm.getQuantity() > FinalValues.FINAL_INVENTORY) {
            throw new ApiException("Quantity should not exceed "+ FinalValues.FINAL_INVENTORY);
        } else if (orderItemForm.getBarcode().length() > FinalValues.MAXIMUM_LENGTH) {
            throw new ApiException("Barcode should not exceed "+ FinalValues.MAXIMUM_LENGTH +" letters");
        }
    }

    public static void normalizeInventoryForm(InventoryForm inventoryForm) {
        inventoryForm.setBarcode(inventoryForm.getBarcode().trim().toLowerCase());
    }

    public static List<OrderError> validateOrderFormList(List<OrderItemForm> orderItemFormList) {
        List<OrderError> orderErrorList = new ArrayList<>();
        Set<String> barcodes = new HashSet<>();
        int index = 0;
        for(OrderItemForm orderItemForm : orderItemFormList) {
            try{
                if(barcodes.contains(orderItemForm.getBarcode())){
                    throw new ApiException("This product has already been added to this order");
                }
                barcodes.add(orderItemForm.getBarcode());
                validateOrderForm(orderItemForm);
            }catch (ApiException apiException){
                OrderError orderError = new OrderError();
                orderError.setMessage(apiException.getMessage());
                orderErrorList.add(orderError);
            }
            index++;
        }
        return orderErrorList;
    }


    public static ZonedDateTime parseStartDate(String startDate) {
        if(startDate.isEmpty()){
            return ZonedDateTime.parse(FinalValues.START_DATE);
        } else {
            return ZonedDateTime.parse(startDate);
        }
    }

    public static ZonedDateTime parseEndDate(String endDate) {
        if(endDate.isEmpty()){
            return ZonedDateTime.now();
        } else {
            return ZonedDateTime.parse(endDate);
        }
    }
}
