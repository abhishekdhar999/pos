package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData {
    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Double sellingPrice;
}
