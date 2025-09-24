package org.example.dao;

import org.example.enums.OrderStatus;
import org.example.models.form.OrderFiltersForm;
import org.example.pojo.OrderPojo;
import org.example.utils.FinalValues;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class OrderDao {

    @PersistenceContext
    private EntityManager em;

    public Integer addOrder(OrderPojo orderPojo){
        em.persist(orderPojo);
        return orderPojo.getId();
    }

    public void updateOrder(OrderPojo orderPojo){
        em.merge(orderPojo);
    }

    public List<OrderPojo> getAllOrders(OrderFiltersForm orderFiltersForm){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> orderPojo = cq.from(OrderPojo.class);
        List<Predicate> predicates = new ArrayList<>();
        if(!Objects.isNull(orderFiltersForm.getOrderId())){
            predicates.add(cb.equal(orderPojo.get("id"), orderFiltersForm.getOrderId()));
        }
        if(!orderFiltersForm.getStatus().isEmpty()){
            predicates.add(cb.equal(orderPojo.get("status"), OrderStatus.valueOf(orderFiltersForm.getStatus())));
        }
        predicates.add(cb.between(orderPojo.get("dateTime"), ZonedDateTime.parse(orderFiltersForm.getStartDate()),ZonedDateTime.parse(orderFiltersForm.getEndDate())));
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(orderPojo.get("id")));
        TypedQuery<OrderPojo> query = em.createQuery(cq);
        query.setMaxResults(orderFiltersForm.getSize());
        query.setFirstResult(orderFiltersForm.getPage()* orderFiltersForm.getSize());
        return query.getResultList();
    }

    public OrderPojo getById(Integer orderId){
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);
Root<OrderPojo> orderPojo = cq.from(OrderPojo.class);
cq.select(orderPojo).where(cb.equal(orderPojo.get("id"), orderId));
TypedQuery<OrderPojo> query = em.createQuery(cq);
return query.getSingleResult();
    }

public List<OrderPojo> getOrderBetweenDatesStatusFulfillable(ZonedDateTime startDate,ZonedDateTime endDate){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> orderPojo = cq.from(OrderPojo.class);

    Predicate datePredicate = cb.between(orderPojo.get("dateTime"), startDate, endDate);
    Predicate statusPredicate = orderPojo.get("status").in(
            OrderStatus.FULFILLABLE, OrderStatus.INVOICED
    );
    cq.select(orderPojo).where(cb.and(datePredicate, statusPredicate));
    return em.createQuery(cq).getResultList();
}

    public Long getTotalCount(OrderFiltersForm orderFiltersForm) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<OrderPojo> orderPojo = cq.from(OrderPojo.class);
        List<Predicate> predicates = new ArrayList<>();
        if (orderFiltersForm.getOrderId() != null) {
            predicates.add(cb.equal(orderPojo.get("id"), orderFiltersForm.getOrderId()));
        }
        if (orderFiltersForm.getStatus() != null && !orderFiltersForm.getStatus().isEmpty()) {
            predicates.add(cb.equal(orderPojo.get("status"), OrderStatus.valueOf(orderFiltersForm.getStatus())));
        }
        if (orderFiltersForm.getStartDate() != null && orderFiltersForm.getEndDate() != null) {
            predicates.add(cb.between(
                    orderPojo.get("dateTime"),
                    ZonedDateTime.parse(orderFiltersForm.getStartDate()),
                    ZonedDateTime.parse(orderFiltersForm.getEndDate())
            ));
        }
        cq.select(cb.count(orderPojo));
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Long> query = em.createQuery(cq);
        return query.getSingleResult();
    }

}
