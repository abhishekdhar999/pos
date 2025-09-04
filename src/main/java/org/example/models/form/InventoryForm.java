package org.example.models.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {
    @NotNull
    private String barcode;
    @NotNull
    private Integer quantity;
}
