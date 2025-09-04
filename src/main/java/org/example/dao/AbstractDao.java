package org.example.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;


public abstract class AbstractDao extends HibernateDaoSupport {
    
//    @Autowired
//    public void setSessionFactory(SessionFactory sessionFactory) {
//        super.setSessionFactory(sessionFactory);
//    }
}

