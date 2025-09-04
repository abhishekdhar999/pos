package org.example.dao;

import org.example.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderItemDao {
    private static final String getAllOrderItemsQuery = "select p from OrderItemPojo p";
    private static final String getByOrderIdQuery = "select p from OrderItemPojo p where orderId=:orderId";

    @PersistenceContext
    private EntityManager em;

    public void addOrderItem(OrderItemPojo orderItemPojo){
        em.persist(orderItemPojo);
    }

    public List<OrderItemPojo> getAllOrderItems() {
        Query query = em.createQuery(getAllOrderItemsQuery);
        return query.getResultList();
    }

    public List<OrderItemPojo> getByOrderId(Integer orderId) {
        Query query = em.createQuery(getByOrderIdQuery);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
}
