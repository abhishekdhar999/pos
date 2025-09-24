package org.example.InvoiceClient;

import org.example.config.ApplicationProperties;
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
    @Autowired
    private ApplicationProperties applicationProperties;

    String GENERATE_INVOICE = "/generate";
    String DOWNLOAD_INVOICE = "/download/";

    public InvoiceResponse generateInvoice(InvoiceRequest request) {
        String url = applicationProperties.getInvoiceBaseUrl() + GENERATE_INVOICE;

        InvoiceResponse resp = restTemplate.postForObject(url, request, InvoiceResponse.class);
        return resp;
    }

    public InvoiceResponse downloadInvoice(Integer orderId) {
String url = applicationProperties.getInvoiceBaseUrl() + DOWNLOAD_INVOICE + orderId;
        byte[] pdfBytes = restTemplate.getForObject(url, byte[].class, orderId);

        String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfBytes);

        InvoiceResponse resp = new InvoiceResponse();
        resp.setBase64Pdf(base64Pdf);
        resp.setInvoiceId(orderId); // Set the order ID as invoice ID
        
        return resp;
    }

    public List<InvoiceData> getInvoice(ZonedDateTime startDate, ZonedDateTime endDate) {
        String url =  applicationProperties.getInvoiceBaseUrl() + "/get?startDate="+startDate.toString()+"&endDate="+endDate.toString();

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
