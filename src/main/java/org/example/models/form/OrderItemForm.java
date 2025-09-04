package org.example.models.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemForm {
    @NotNull
    private String barcode;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double sellingPrice;
}