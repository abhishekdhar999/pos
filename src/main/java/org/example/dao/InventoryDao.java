package org.example.dao;


import org.example.dto.ApiException;
import org.example.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
@Transactional
public class InventoryDao extends AbstractDao {

    private static final String select_id = "select inventory from InventoryPojo inventory where inventory.productId = :id";

    @PersistenceContext
    private  EntityManager em;

    public  InventoryPojo getInventoryByProductId(int id) throws ApiException {
        TypedQuery<InventoryPojo> query = em.createQuery(select_id, InventoryPojo.class);
        query.setParameter("id", id);
       return query.getSingleResult();

    }

    public void createInventory(InventoryPojo inventoryPojo) throws ApiException{
        em.persist(inventoryPojo);
    }
}
