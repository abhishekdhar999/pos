package org.example.api;


import org.example.dao.ClientDao;
import org.example.dto.ApiException;
import org.example.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ClientApi {

    @Autowired
    private ClientDao clientDao;


    public void addClient(ClientPojo p){
        clientDao.insertClient(p);
    }

public ClientPojo getClientById(int id) throws ApiException  {

    return clientDao.getSingleClient(id);
}

public ClientPojo getClientByName(String name) throws ApiException  {

    return clientDao.getClientByName(name);
}

public List<ClientPojo> getAllClients() throws ApiException  {
    return clientDao.getAllClients();
}

public  void clientUpdate(ClientPojo pojo) {
    clientDao.updateClient(pojo);
}


}
