package org.example.dto;


import org.example.api.ClientApi;
import org.example.dao.ClientDao;
import org.example.models.ClientData;
import org.example.models.ClientForm;
import org.example.pojo.ClientPojo;
import org.example.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;


import static org.example.dto.DtoHelperClient.*;

@Service
public class ClientDto {

    @Autowired
    private ClientApi clientApi;


    public void operationOnClient(ClientForm form) throws ApiException {

        ClientPojo clientByName = clientApi.getClientByName(form.getName());

        //  checking if name already exist
        checkAlreadyExist(clientByName);

        //   converting form into pojo
        ClientPojo client = convert(form);

        // converting into lowercase
        normalize(client);

        // checking if name is empty
        checkNameIsValid(client);

      clientApi.addClient(client);
    }

//    @Transactional(rollbackOn = ApiException.class)
    public ClientData get(int id) throws ApiException {
      ClientPojo client =   clientApi.getClientById(id);
        checkExist(client);
       ClientData clientdata = convert(client);

        return clientdata;
    }

    public ClientData getClientByName(String name) throws ApiException {
        ClientPojo client = clientApi.getClientByName(name);
        checkExist(client);
        ClientData clientdata = convert(client);
        return clientdata;
    }

    public List<ClientData> getAll() throws ApiException {
        List<ClientPojo> allClientspojo = clientApi.getAllClients();
        List<ClientData> allClientsdata = new ArrayList<>();
        for(ClientPojo client : allClientspojo){
            ClientData clientdata = convert(client);
            allClientsdata.add(clientdata);
        }
        return allClientsdata;
    }

public void updateOperationOnClient(ClientForm form,int id) throws ApiException {

    ClientPojo client = clientApi.getClientById(id);

    checkExist(client);

  ClientPojo updatedClient =  updateClient(form,client);

clientApi.clientUpdate(updatedClient);
}




}
