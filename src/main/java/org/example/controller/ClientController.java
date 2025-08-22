package org.example.controller;

import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.models.data.ClientData;
import org.example.models.form.ClientForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.dto.DtoHelperClient.convert;


@Api
@RestController
@RequestMapping(value = "/api")
public class ClientController {


    @Autowired
    private ClientDto clientDto;

    @ApiOperation(value = "client is being created")
    @RequestMapping(value = "/clients", method = RequestMethod.POST)
    public void createClient(@RequestBody ClientForm form) throws ApiException {

        clientDto.operationOnClient(form);
    }
@ApiOperation(value="fetching client by id")
@RequestMapping(value = "/clients/{id}",method = RequestMethod.GET)
    public ClientData getClientById(@PathVariable int id) throws ApiException {

      return clientDto.get(id);

    }

    @ApiOperation(value = "get client by name")
    @RequestMapping(value = "/clients/{name}",method = RequestMethod.GET)
    public ClientData getClientByName(@PathVariable String name) throws ApiException {

        return clientDto.getClientByName(name);
    }

    @ApiOperation(value = "get all clients")
    @RequestMapping(value = "/clients/all",method = RequestMethod.GET)
    public List<ClientData> getAllClients() throws ApiException {
        return clientDto.getAll();
    }

    @ApiOperation(value = "update client by id")
    @RequestMapping(value = "/clients/update/{id}",method = RequestMethod.PUT)
    public void updateClientById(@PathVariable int id,@RequestBody ClientForm form ) throws ApiException {

clientDto.updateOperationOnClient(form,id);
    }


}










