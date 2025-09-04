package org.example.models.data;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderData {
    private Integer id;
    private String dateTime;
    private OrderStatus status;
    private List<OrderItemData> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItemData orderItemData){
        this.orderItems.add(orderItemData);
    }
}
