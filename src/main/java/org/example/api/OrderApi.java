package org.example.api;

import org.example.dao.OrderDao;
import org.example.dto.ApiException;
import org.example.models.form.OrderFiltersForm;
import org.example.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderApi {
    @Autowired
    private OrderDao orderDao;

    public Integer addOrder(OrderPojo orderPojo){
        return orderDao.addOrder(orderPojo);
    }

    public void updateOrder(OrderPojo orderPojo){
        orderDao.updateOrder(orderPojo);
    }

    public List<OrderPojo> getAllOrders(OrderFiltersForm orderFiltersForm) throws ApiException {
        return orderDao.getAllOrders(orderFiltersForm);
    }

    public OrderPojo getById(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderDao.getById(orderId);
        if(Objects.isNull(orderPojo)){
            throw new ApiException("Order with id '"+orderId+"' doesn't exists");
        }
        return orderPojo;
    }



    public List<OrderPojo> getOrderBetweenDatesStatusFulfillable(ZonedDateTime startDate, ZonedDateTime endDate){
        return orderDao.getOrderBetweenDatesStatusFulfillable(startDate,endDate);
    }

    public Long getTotalCount(OrderFiltersForm orderFiltersForm) throws ApiException {
        return orderDao.getTotalCount(orderFiltersForm);
    }
}
