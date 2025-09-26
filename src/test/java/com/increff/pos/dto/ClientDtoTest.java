package com.increff.pos.dto;

import static org.junit.Assert.assertEquals;

import com.increff.pos.AbstractUnitTest;
import org.example.dao.ClientDao;
import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.models.data.ClientData;
import org.example.models.form.ClientForm;
import org.example.pojo.ClientPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ClientDtoTest extends AbstractUnitTest {

    @Autowired
    private ClientDto clientDto;
    @Autowired
    private ClientDao clientDao;

    private ClientForm clientForm;
    @Before
    public void setup() throws ApiException {
        clientForm = TestHelper.createClientForm("    DEMO_CLIENT    ");
        clientDto.add(clientForm);
    }

    @Test
    public void testCreateClient() throws ApiException {
        ClientPojo clientPojo = clientDao.getAll(0, 1).get(0);
        assertEquals(clientPojo.getName(), clientForm.getName());
    }

    @Test
    public void testUpdateClient() throws ApiException {
        ClientPojo savedClient = clientDao.getAll(0, 1).get(0);
        ClientForm updateForm = TestHelper.createUpdateClientForm("updated_demo_client");
        clientDto.update(savedClient.getId(), updateForm);
        ClientPojo updatedClient = clientDao.getById(savedClient.getId());
        assertEquals("updated_demo_client", updatedClient.getName());

    }

    @Test
    public void getAllClients() throws ApiException {
        List<ClientData> clients = clientDto.getAll(0, 1);
        assertEquals(clientForm.getName(), clients.get(0).getName());

    }

    @Test
    public void getClientById() throws ApiException {
        ClientPojo clientPojo = clientDao.getAll(0, 1).get(0);
        assertEquals(clientPojo.getName(), clientForm.getName());
    }

}
