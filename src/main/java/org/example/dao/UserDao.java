package org.example.dao;

import org.example.pojo.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
@Transactional
public class UserDao {
    @PersistenceContext
    private EntityManager em;

    private static final String getByEmailQuery = "select p from UserPojo p where p.email=:email";
    public UserPojo getByEmail(String email) {
        Query query = em.createQuery(getByEmailQuery);
        query.setParameter("email", email);
        try{
            return (UserPojo) query.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    public void add(UserPojo userPojo) {
        em.persist(userPojo);
    }
}
