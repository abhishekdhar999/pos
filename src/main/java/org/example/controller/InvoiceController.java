package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.InvoiceDto;
import org.example.models.data.InvoiceData;
import org.example.models.form.InvoiceFilterForm;
import org.example.utils.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceDto invoiceDto;

    @RequestMapping(path = "generate/{orderId}", method = RequestMethod.POST)
    public InvoiceResponse   generateInvoice(@PathVariable Integer orderId) throws ApiException {
      return invoiceDto.generateInvoice(orderId);
    }

    @ApiOperation(value = "downloading the invoice")
    @RequestMapping(path = "/download/{id}",method = RequestMethod.GET)
    public void downloadInvoice(@PathVariable Integer id,HttpServletResponse response ) throws ApiException {
        invoiceDto.downloadInvoice( id,response);
    }

    @ApiOperation(value = "get invoices")
    @RequestMapping(path="/get",method = RequestMethod.GET)
    public List<InvoiceData> getInvoices(@ModelAttribute InvoiceFilterForm filterForm) throws ApiException {
return invoiceDto.getInvoices(filterForm);
    }

}