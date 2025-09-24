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

    private static  String getAllQuery = "select p from ProductPojo p ";
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

    public List<ProductPojo> getAll(Integer page, Integer size, String keyword){
        StringBuilder newQuery = new StringBuilder(getAllQuery);
        if(!keyword.isEmpty()){
            newQuery.append(" where lower(p.barcode) like :keyword or lower(p.name) like :keyword");
        }
        TypedQuery<ProductPojo> query = em.createQuery(newQuery.toString(), ProductPojo.class);
        if(!keyword.isEmpty()){
            keyword = "%"+keyword.toLowerCase().trim()+"%";
            query.setParameter("keyword", keyword);
        }
        query.setFirstResult(page*size);
        query.setMaxResults(size);

        return query.getResultList();
    }


//todo dont write exception in dao
    public ProductPojo getByBarcode(String barcode) {
        Query query = em.createQuery(getByBarcodeQuery);
        query.setParameter("barcode", barcode);
        try{
          return (ProductPojo) query.getSingleResult();
//            List<ProductPojo> results = query.getResultList();
//                return results.get(0);
        }catch (NoResultException noResultException){

            return null;
        }
    }

    public ProductPojo getById(Integer id) throws ApiException {
        Query query = em.createQuery(getByIdQuery);
        query.setParameter("id", id);
        try{
        return  (ProductPojo) query.getSingleResult();
        }catch (NoResultException noResultException){
            return null;
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
