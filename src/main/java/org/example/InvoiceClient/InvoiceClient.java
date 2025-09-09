package org.example.InvoiceClient;

import org.example.models.data.InvoiceRequest;
import org.example.utils.InvoiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.List;

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

    public InvoiceResponse downloadInvoice(Integer orderId) {
        String url = "http://localhost:8000/pos/api/invoice/download/{orderId}";
        // adjust port/context-path for invoice-app

        // POST request -> Base64 string as response
        InvoiceResponse resp = restTemplate.getForObject(url, InvoiceResponse.class, orderId);
        return resp;
    }

    public List<Integer> getInvoice(ZonedDateTime startDate, ZonedDateTime endDate) {
        String url = "http://localhost:8000/pos/api/invoice/get"+"?startDate="+startDate.toString()+"&endDate="+endDate.toString();
        ResponseEntity<List<Integer>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Integer>>() {}
        );
        return response.getBody();
    }
}
