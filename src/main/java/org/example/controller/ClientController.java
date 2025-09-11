package org.example.controller;

import org.example.dto.ApiException;
import org.example.dto.ClientDto;
import org.example.models.data.ClientData;
import org.example.models.form.ClientForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.pojo.ClientPojo;
import org.example.utils.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api
@RestController
@RequestMapping(value = "/api/clients")
public class ClientController {
    @Autowired
    private ClientDto clientDto;

    @ApiOperation("adds a client")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void add(@RequestBody ClientForm client) throws ApiException {
        clientDto.add(client);
    }

    @ApiOperation("updates a client")
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Integer id,  @RequestBody ClientForm client) throws ApiException{
        clientDto.update(id, client);
    }

    @ApiOperation("gets all the client")
    @RequestMapping(method = RequestMethod.GET)
    public PaginatedResponse<ClientData> getAll(@RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size){
        List<ClientData> clientDataList = clientDto.getAll(page, size);
Long total = clientDto.getTotalCount();
        PaginatedResponse<ClientData> response = new PaginatedResponse<>();
        response.setPage(page);
        response.setSize(size);
        response.setData(clientDataList);
        response.setTotalPages(total);
        return response;
    }

    @RequestMapping(path = "/{id}" , method = RequestMethod.GET)
    public ClientData getById(@PathVariable Integer id) throws ApiException{
        return clientDto.getById(id);
    }

    @RequestMapping(path = "/count", method = RequestMethod.GET)
    public Long getTotalCount(){
        return clientDto.getTotalCount();
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public List<String> searchByName(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String name){
        return clientDto.searchByName(page, size, name);
    }



}










