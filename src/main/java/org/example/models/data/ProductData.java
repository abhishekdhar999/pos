package org.example.models.data;

import org.example.models.form.ProductForm;

@lombok.Data
public class ProductData extends ProductForm {
    int id;
int quantity;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
