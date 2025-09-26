package com.increff.pos.dto;

import org.example.dto.*;
import org.example.models.data.*;
import org.example.utils.InvoiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class InvoiceDtoTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InvoiceDto invoiceDto;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGenerateInvoice() throws ApiException {
        InvoiceResponse fakeResponse = new InvoiceResponse();
        fakeResponse.setInvoiceId(123);
        fakeResponse.setBase64Pdf("dummyBase64Pdf");
        fakeResponse.setFilePath("/tmp/invoice123.pdf");

        when(restTemplate.postForObject(anyString(), any(InvoiceRequest.class), eq(InvoiceResponse.class)))
                .thenReturn(fakeResponse);

        InvoiceResponse response = invoiceDto.generateInvoice(1);
        assertNotNull(response);
        assertEquals(Integer.valueOf(123),response.getInvoiceId());
        assertEquals("dummyBase64Pdf", response.getBase64Pdf());
        assertEquals("/tmp/invoice123.pdf", response.getFilePath());
    }

}
