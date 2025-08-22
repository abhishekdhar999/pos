package org.example.dao;

import org.example.dto.ApiException;
import org.example.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
@Transactional
public class ProductDao {

    private static final String select_all = "select product from ProductPojo product ";
private static final String select_id = "select product from ProductPojo product where product.id = :id ";
    @PersistenceContext
    private  EntityManager em;

//    public static void addProduct(List<ProductPojo> productPojoList) throws ApiException {
//
//    for(ProductPojo productPojo : productPojoList){
//        em.persist(productPojo);
//    }
//
//}

//public  static List<ProductPojo> getAllProducts(){
//    TypedQuery<ProductPojo> query = em.createQuery(select_all, ProductPojo.class);
//    return query.getResultList();
//
//}

public void createProduct(ProductPojo pojo) throws ApiException {
     em.persist(pojo);

}

public ProductPojo getProductById(int id) throws ApiException {
    TypedQuery<ProductPojo> query = em.createQuery(select_id, ProductPojo.class);
    query.setParameter("id", id);
    return query.getSingleResult();
}
}
