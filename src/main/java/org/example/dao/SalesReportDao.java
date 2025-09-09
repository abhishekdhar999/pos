package org.example.dao;

import org.example.dto.ApiException;
import org.example.pojo.DaySalesReportPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Transactional
public class SalesReportDao {
    private static final String getDailySalesReportQuery = "select p from DaySalesReportPojo p where p.dateTime>=:startDate and p.dateTime<=:endDate order by p.id desc";

    @PersistenceContext
    private EntityManager em;

    public void addDaySalesReport(DaySalesReportPojo daySalesReportPojo) throws ApiException{
        em.persist(daySalesReportPojo);
    }

    public List<DaySalesReportPojo> getDaySalesReports(ZonedDateTime startDate, ZonedDateTime endDate, Integer page, Integer size) throws ApiException{
        Query query = em.createQuery(getDailySalesReportQuery);
        query.setFirstResult(page*size);
        query.setMaxResults(size);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
}
