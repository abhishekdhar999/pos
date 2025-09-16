package org.example.InvoiceClient;

import org.example.models.data.InvoiceData;
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
    }

    public InvoiceResponse downloadInvoice(Integer orderId) {
        String url = "http://localhost:8000/pos/api/invoice/download/{orderId}";

        // GET request -> Raw PDF bytes as response
        byte[] pdfBytes = restTemplate.getForObject(url, byte[].class, orderId);
        
        // Convert PDF bytes to Base64 string
        String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfBytes);
        
        // Create InvoiceResponse object
        InvoiceResponse resp = new InvoiceResponse();
        resp.setBase64Pdf(base64Pdf);
        resp.setInvoiceId(orderId); // Set the order ID as invoice ID
        
        return resp;
    }

    public List<InvoiceData> getInvoice(ZonedDateTime startDate, ZonedDateTime endDate) {
        String url = "http://localhost:8000/pos/api/invoice/get?startDate= "+startDate.toString()+"&endDate="+endDate.toString();

        ResponseEntity<List<InvoiceData>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InvoiceData>>() {}
        );
        System.out.println("response"+response.getBody());
        return response.getBody();
    }
}
