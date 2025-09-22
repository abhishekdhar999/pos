package org.example.dao;

import org.example.enums.OrderStatus;
import org.example.models.form.OrderFiltersForm;
import org.example.pojo.OrderPojo;
import org.example.utils.FinalValues;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class OrderDao {
    private static final String getAllOrdersQuery = "select p from OrderPojo p";
    private static final String getByIdQuery = "select p from OrderPojo p where id=:id";
    private static final String updateQuery = "update OrderPojo p set p.dateTime=:dateTime, p.status=:status where id=:id";
    private static final String getBetweenDatesQuery = "select p from OrderPojo p where p.dateTime between :startDate and :endDate";
    private static final String getTotalCountQuery = "select count(p) from OrderPojo p where p.dateTime between :startDate and :endDate";
private static final String getCount="select count(p) from OrderPojo p";
private static final String getBetweenDatesQueryWhereStatusFulfillable = "select p from OrderPojo p where p.dateTime between :startDate and :endDate and p.status IN :statuses";
    @PersistenceContext
    private EntityManager em;

    public Integer addOrder(OrderPojo orderPojo){
        em.persist(orderPojo);
        return orderPojo.getId();
    }


    public void updateOrder(OrderPojo orderPojo){
        em.merge(orderPojo);
    }

    public void update(Integer id, OrderPojo orderPojo){
        Query query = em.createQuery(updateQuery);
        query.setParameter("dateTime", orderPojo.getDateTime());
        query.setParameter("status", orderPojo.getStatus());
        query.setParameter("id", id);

        query.executeUpdate();
    }


    public List<OrderPojo> getAllOrders(OrderFiltersForm orderFiltersForm) {
        try {
            String editedQuery = new String(getBetweenDatesQuery);
            if(!Objects.isNull(orderFiltersForm.getOrderId())){
                editedQuery+=" and p.id=:orderId";
            }
            if(!orderFiltersForm.getStatus().isEmpty()){
                editedQuery+=" and p.status=:status";
            }
            editedQuery+=" order by p.id desc";

            Query query = em.createQuery(editedQuery);
            query.setMaxResults(orderFiltersForm.getSize());
            query.setFirstResult(orderFiltersForm.getPage()* orderFiltersForm.getSize());
            
            // Handle start date with better error handling
            if(orderFiltersForm.getStartDate().isEmpty()){
                query.setParameter("startDate", ZonedDateTime.parse(FinalValues.START_DATE));
            } else {
                try {
                    query.setParameter("startDate", ZonedDateTime.parse(orderFiltersForm.getStartDate()));
                } catch (Exception e) {
                    System.err.println("Error parsing startDate: " + orderFiltersForm.getStartDate() + " - " + e.getMessage());
                    query.setParameter("startDate", ZonedDateTime.parse(FinalValues.START_DATE));
                }
            }
            
            // Handle end date with better error handling
            if(orderFiltersForm.getEndDate().isEmpty()){
                query.setParameter("endDate", ZonedDateTime.now());
            } else {
                try {
                    query.setParameter("endDate", ZonedDateTime.parse(orderFiltersForm.getEndDate()));
                } catch (Exception e) {
                    System.err.println("Error parsing endDate: " + orderFiltersForm.getEndDate() + " - " + e.getMessage());
                    query.setParameter("endDate", ZonedDateTime.now());
                }
            }
            
            if(!Objects.isNull(orderFiltersForm.getOrderId())){
                query.setParameter("orderId", orderFiltersForm.getOrderId());
            }
            if(!orderFiltersForm.getStatus().isEmpty()){
                query.setParameter("status", OrderStatus.valueOf(orderFiltersForm.getStatus()));
            }
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error in getAllOrders: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public OrderPojo getById(Integer orderId) {
        Query query = em.createQuery(getByIdQuery);
        query.setParameter("id", orderId);
        try{
            return (OrderPojo)query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }



    public List<OrderPojo> getBetweenDates(ZonedDateTime startDate, ZonedDateTime endDate){
        Query query = em.createQuery(getBetweenDatesQuery);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        List<OrderPojo> orderPojoList = query.getResultList();
        return orderPojoList;
    }

    public List<OrderPojo> getOrderBetweenDatesStatusFulfillable(ZonedDateTime startDate, ZonedDateTime endDate){
        Query query = em.createQuery(getBetweenDatesQueryWhereStatusFulfillable);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("statuses", Arrays.asList(OrderStatus.FULFILLABLE, OrderStatus.INVOICED));
        List<OrderPojo> orderPojoList = query.getResultList();
        return orderPojoList;
    }

//    public Long getTotalCount(OrderFiltersForm orderFiltersForm) {
//        try {
//            String editedQuery = new String(getTotalCountQuery);
//            if(!Objects.isNull(orderFiltersForm.getOrderId())){
//                editedQuery+=" and p.id=:orderId";
//            }
//            if(!orderFiltersForm.getStatus().isEmpty()){
//                editedQuery+=" and p.status=:status";
//            }
//
//            Query query = em.createQuery(editedQuery);
//
//            // Handle start date with better error handling
//            if(orderFiltersForm.getStartDate().isEmpty()){
//                query.setParameter("startDate", ZonedDateTime.parse(FinalValues.START_DATE));
//            } else {
//                try {
//                    query.setParameter("startDate", ZonedDateTime.parse(orderFiltersForm.getStartDate()));
//                } catch (Exception e) {
//                    System.err.println("Error parsing startDate in getTotalCount: " + orderFiltersForm.getStartDate() + " - " + e.getMessage());
//                    query.setParameter("startDate", ZonedDateTime.parse(FinalValues.START_DATE));
//                }
//            }
//
//            // Handle end date with better error handling
//            if(orderFiltersForm.getEndDate().isEmpty()){
//                query.setParameter("endDate", ZonedDateTime.now());
//            } else {
//                try {
//                    query.setParameter("endDate", ZonedDateTime.parse(orderFiltersForm.getEndDate()));
//                } catch (Exception e) {
//                    System.err.println("Error parsing endDate in getTotalCount: " + orderFiltersForm.getEndDate() + " - " + e.getMessage());
//                    query.setParameter("endDate", ZonedDateTime.now());
//                }
//            }
//
//            if(!Objects.isNull(orderFiltersForm.getOrderId())){
//                query.setParameter("orderId", orderFiltersForm.getOrderId());
//            }
//            if(!orderFiltersForm.getStatus().isEmpty()){
//                query.setParameter("status", OrderStatus.valueOf(orderFiltersForm.getStatus()));
//            }
//            return (Long) query.getSingleResult();
//        } catch (Exception e) {
//            System.err.println("Error in getTotalCount: " + e.getMessage());
//            e.printStackTrace();
//            return 0L;
//        }
//    }

    public Long getCount(){
        Query query = em.createQuery(getCount);
        return (Long) query.getSingleResult();
    }
}
