package org.example.models;

public class ProductForm {
    String name;
    String barcode;
    double price;
    String imageUrl;
String category;
String sku;
String clientName;


public String getClientName() {
	return clientName;
}
public void setClientName(String clientName) {
    this.clientName = clientName;
}


public String getName() {
    return name;
}
public void setName(String name) {
    this.name = name;
}


    public String getBarcode() {
    return barcode;
}
    public void setBarcode(String barcode) {
    this.barcode = barcode;
    }


    public double getPrice() {
    return price;
    }
    public void setPrice(double price) {
    this.price = price;
    }


    public String getImageUrl() {
    return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    }


    public int getClientId() {
    return clientId;
    }
    public void setClientId(int clientId) {
    this.clientId = clientId;
    }

    public String getCategory() {
    return category;
    }
    public void setCategory(String category) {
    this.category = category;
    }

    public String getSku() {
    return sku;
    }
    public void setSku(String sku) {
    this.sku = sku;
    }


}
