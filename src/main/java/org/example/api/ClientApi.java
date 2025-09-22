package org.example.api;


import org.example.dao.ClientDao;
import org.example.dto.ApiException;
import org.example.pojo.ClientPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ClientApi {

    @Autowired
    private ClientDao clientDao;

    public void add(ClientPojo clientPojo) throws ApiException{
        ClientPojo clientPojoCheck = getByName(clientPojo.getName());
        if (Objects.nonNull(clientPojoCheck)) {
            throw new ApiException("Client already exists");
        }
        clientDao.add(clientPojo);
    }

    public List<ClientPojo> getAll(Integer page, Integer size) {
        List<ClientPojo> clientPojoList = clientDao.getAll(page, size);
        return clientPojoList;
    }
//todo dont need to use the update method
    public void update(Integer id, String name) throws ApiException{
        ClientPojo clientPojo1 = getByName(name);
        if (Objects.nonNull(clientPojo1)) {
            throw new ApiException("client name already exists");
        }
        ClientPojo clientPojo = getById(id);
        clientPojo.setName(name);
    }
//todo check for getbyid method refine the code
    public ClientPojo getById(Integer id) throws ApiException{
        ClientPojo clientPojo = clientDao.getById(id);
        if(Objects.isNull(clientPojo)){
            throw new ApiException("Client id doesn't exists");
        }
        return clientPojo;
    }
    public ClientPojo getByName(String name) throws ApiException{
       return clientDao.getByName(name);
    }
    public List<String> searchByName(Integer page, Integer size, String name) {
        return clientDao.searchByName(page, size, name);
    }


}
