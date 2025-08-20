package org.example.dao;
import java.util.List;
import org.example.pojo.ClientPojo;
import org.example.dto.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
@Transactional
public class ClientDao extends AbstractDao {


    private static final String delete_id = "delete from ClientPojo client where id=:id";
private static final String select_id = "select client from ClientPojo client where client.id=:id";
private static final String select_name = "select client from ClientPojo client where client.name=:name";
private static final String select_all = "select client from ClientPojo client ";


@PersistenceContext
private EntityManager em;


  public void insertClient(ClientPojo p){
    em.persist(p);

    }

    public ClientPojo getSingleClient(int id) throws ApiException {
TypedQuery<ClientPojo> query = em.createQuery(select_id, ClientPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public ClientPojo getClientByName(String name) throws ApiException {
        TypedQuery<ClientPojo> query = em.createQuery(select_name, ClientPojo.class);
        query.setParameter("name", name);
        return getSingle(query);
    }

    public List<ClientPojo> getAllClients() throws ApiException {
      TypedQuery<ClientPojo> query = em.createQuery(select_all, ClientPojo.class);
      return query.getResultList();
    }

    public void updateClient(ClientPojo p) {
      em.merge(p);
    }
}
