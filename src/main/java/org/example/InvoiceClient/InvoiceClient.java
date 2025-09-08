package org.example.InvoiceClient;

import org.example.models.data.InvoiceRequest;
import org.example.utils.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvoiceClient {

    @Autowired
    private RestTemplate restTemplate;


    public InvoiceResponse generateInvoice(InvoiceRequest request) {
        String url = "http://localhost:8000/pos/api/invoice/generate";
        // adjust port/context-path for invoice-app

        // POST request -> Base64 string as response
        InvoiceResponse resp = restTemplate.postForObject(url, request, InvoiceResponse.class);
        return resp;
//        return restTemplate.postForObject(url, request, String.class);
    }

    public String downloadInvoice(Integer orderId) {
        String url = "http://localhost:8000/invoice/download/{orderId}";
        // adjust port/context-path for invoice-app

        // POST request -> Base64 string as response
        return restTemplate.getForObject(url,String.class);
    }
}
