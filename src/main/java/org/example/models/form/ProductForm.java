package org.example.models.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    @NotNull
    private String barcode;
    @NotNull
    private String clientName;
    @NotNull
    private String name;
    @NotNull
    private Double price;
    @NotNull
    private String imageUrl;
}
