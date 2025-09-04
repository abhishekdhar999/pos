package org.example.dao;

import org.example.dto.ApiException;
import org.example.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ProductDao {

    private static final String getAllQuery = "select p from ProductPojo p";
    private static final String getByBarcodeQuery = "select p from ProductPojo p where barcode=:barcode";
    private static final String getByIdQuery = "select p from ProductPojo p where id=:id";
    private static final String updateQuery = "update ProductPojo p set p.barcode=:barcode, p.clientId=:clientId, p.name=:name, p.price=:price, p.imageUrl=:imageUrl where id=:id";
    private static final String getTotalCountQuery = "select count(p) from ProductPojo p";
    private static final String searchByBarcodeQuery = "select p.barcode from ProductPojo p where p.barcode like :barcode";

    @PersistenceContext
    private EntityManager em;

    public void add(ProductPojo productPojo){
        em.persist(productPojo);
    }

    public void batchAdd(List<ProductPojo> productPojoList){
        for(ProductPojo productPojo: productPojoList){
            em.persist(productPojo);
        }
    }

    public List<ProductPojo> getAll(Integer page, Integer size, String keyword){
        String newQuery = getAllQuery;
//        if(!keyword.isEmpty()){
//            newQuery+="where p.barcode like :keyword or p.name like :keyword";
//        }
        TypedQuery<ProductPojo> query = em.createQuery(newQuery, ProductPojo.class);
        if(!keyword.isEmpty()){
            keyword = "%"+keyword.toLowerCase().trim()+"%";
            query.setParameter("keyword", keyword);
        }
        query.setFirstResult(page*size);
        query.setMaxResults(size);

        return query.getResultList();
    }



    public ProductPojo getByBarcode(String barcode){
        Query query = em.createQuery(getByBarcodeQuery);
        query.setParameter("barcode", barcode);
        try{
            List<ProductPojo> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                // Handle multiple results - return the first one or throw exception
                throw new RuntimeException("Multiple products found with barcode: " + barcode);
            }
        }catch (NoResultException noResultException){

            return null;
        }
    }

    public void update(Integer id, ProductPojo productPojo){
        Query query = em.createQuery(updateQuery);

        query.setParameter("id", id);
        query.setParameter("name", productPojo.getName());
        query.setParameter("clientId", productPojo.getClientId());
        query.setParameter("price", productPojo.getPrice());
        query.setParameter("imageUrl", productPojo.getImageUrl());
        query.setParameter("barcode", productPojo.getBarcode());
        query.executeUpdate();
    }

    public ProductPojo getById(Integer id) throws ApiException {
        Query query = em.createQuery(getByIdQuery);
        query.setParameter("id", id);
        try{
            List<ProductPojo> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                throw new RuntimeException("Multiple products found with id: " + id);
            }
        }catch (NoResultException noResultException){
            return null;
        }
    }

    public Long getTotalCount(){
        Query query = em.createQuery(getTotalCountQuery);
        List<Long> results = query.getResultList();
        if (results.isEmpty()) {
            return 0L;
        } else {
            return results.get(0);
        }
    }

    public List<String> searchByBarcode(Integer page, Integer size, String barcode) {
        Query query = em.createQuery(searchByBarcodeQuery);
        query.setParameter("barcode", "%"+barcode+"%");
        query.setFirstResult(page*size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
