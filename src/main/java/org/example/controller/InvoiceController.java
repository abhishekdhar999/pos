package org.example.controller;

import io.swagger.annotations.Api;
import org.example.dto.ApiException;
import org.example.dto.InvoiceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceDto invoiceDto;

    @RequestMapping(path = "generate/{orderId}", method = RequestMethod.PUT)
    public void generateInvoice(@PathVariable Integer orderId, HttpServletResponse response) throws ApiException {
        invoiceDto.generateInvoice(orderId, response);
    }

}