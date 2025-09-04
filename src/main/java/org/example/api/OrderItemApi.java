package org.example.api;

import org.example.dao.OrderItemDao;
import org.example.dto.ApiException;
import org.example.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderItemApi {
    @Autowired
    private OrderItemDao orderItemDao;

    public void addOrderItem(OrderItemPojo orderItemPojo){
        orderItemDao.addOrderItem(orderItemPojo);
    }

    public List<OrderItemPojo> getAllOrderItems() {
        return orderItemDao.getAllOrderItems();
    }

    public List<OrderItemPojo> getByOrderId(Integer orderId) {
        return orderItemDao.getByOrderId(orderId);
    }
}
