package org.example.models.data;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProductData {
    private Integer id;
    private String barcode;
    private String clientName;
    private String name;
    private Double price;
    private String imageUrl;
    private Integer quantity;
}
