package org.example.dto;


import org.example.api.ClientApi;
import org.example.models.data.ClientData;
import org.example.models.form.ClientForm;
import org.example.pojo.ClientPojo;
import org.example.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.example.dto.DtoHelper.convertClientFormToClientPojo;

@Component
@Service
public class ClientDto {

    @Autowired
    private ClientApi clientApi;

    public void add(ClientForm client) throws ApiException {
        UtilMethods.normalizeClientForm(client);
        UtilMethods.validateClientForm(client);
        ClientPojo clientPojo = convertClientFormToClientPojo(client);
        clientApi.add(clientPojo);
    }

    public List<ClientData> getAll(Integer page, Integer size) {
        List<ClientPojo> clientPojoList = clientApi.getAll(page, size);
        return DtoHelper.convertClientPojoListToClientDataList(clientPojoList);
    }

    public void update(Integer id, ClientForm clientForm) throws ApiException{
        UtilMethods.normalizeClientForm(clientForm);
        UtilMethods.validateClientForm(clientForm);
        clientApi.update(id, clientForm.getName());
    }

    public ClientData getById(Integer id) throws ApiException{
        ClientPojo clientPojo = clientApi.getById(id);
        return DtoHelper.convertClientPojoToClientData(clientPojo);
    }

    public Long getTotalCount(){
        return clientApi.getTotalCount();
    }

    public List<String> searchByName(Integer page, Integer size, String name) {
        return clientApi.searchByName(page, size, name);
    }
}





