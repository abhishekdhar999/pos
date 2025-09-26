package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.InvoiceClient.InvoiceClient;
import org.example.dto.*;
import org.example.flow.InvoiceFlow;
import org.example.models.data.*;
import org.example.utils.InvoiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class InvoiceDtoTest extends AbstractUnitTest {
    @Mock
    private RestTemplate restTemplate;

  @Autowired
  private InvoiceClient invoiceClient;
  @Autowired
  private InvoiceDto invoiceDto;
  @Autowired
  private InvoiceFlow invoiceFlow;
  @Autowired

    @Before
    public void setUp() {
      MockitoAnnotations.initMocks(this);
      invoiceClient.setRestTemplate(restTemplate);
      invoiceFlow.setInvoiceClient(invoiceClient);
      invoiceDto.setInvoiceFlow(invoiceFlow);
    }
    @Test
    public void testGenerateInvoice() throws ApiException {
        List<InvoiceItem> invoiceItemList = new ArrayList<>();
        InvoiceItem invoiceItem = TestHelper.createInvoiceItem("product",10,100.00,1000.00);
        invoiceItemList.add(invoiceItem);
        InvoiceRequest invoiceRequest = TestHelper.createInvoiceRequest(1,"2025-08-01T00:00:00Z","2025-08-01T00:00:00Z",1000.00,invoiceItemList);
        InvoiceResponse fakeResponse = new InvoiceResponse();
        fakeResponse.setInvoiceId(123);
        fakeResponse.setBase64Pdf("dummyBase64Pdf");
        fakeResponse.setFilePath("/tmp/invoice123.pdf");

        when(restTemplate.postForObject(anyString(), any(InvoiceRequest.class), eq(InvoiceResponse.class)))
                .thenReturn(fakeResponse);

        InvoiceResponse response = invoiceClient.generateInvoice(invoiceRequest);
        assertNotNull(response);
        assertEquals(Integer.valueOf(123),response.getInvoiceId());
        assertEquals("dummyBase64Pdf", response.getBase64Pdf());
        assertEquals("/tmp/invoice123.pdf", response.getFilePath());
    }

}
