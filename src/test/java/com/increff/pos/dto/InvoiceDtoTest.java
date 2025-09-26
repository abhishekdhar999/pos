package com.increff.pos.dto;

import com.increff.pos.AbstractUnitTest;
import org.example.InvoiceClient.InvoiceClient;
import org.example.api.OrderApi;
import org.example.dto.*;
import org.example.enums.OrderStatus;
import org.example.flow.InvoiceFlow;
import org.example.flow.OrderFlow;
import org.example.models.data.*;
import org.example.models.form.InvoiceFilterForm;
import org.example.pojo.OrderPojo;
import org.example.utils.InvoiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.increff.pos.dto.TestHelper.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class InvoiceDtoTest extends AbstractUnitTest {
    
    @Mock
    private InvoiceFlow invoiceFlow;
    
    @Mock
    private OrderFlow orderFlow;
    
    @Mock
    private InvoiceClient invoiceClient;
    
    @Mock
    private OrderApi orderApi;
    
    @Mock
    private HttpServletResponse httpServletResponse;
    
    @Mock
    private OutputStream outputStream;

    @Mock
    private ServletOutputStream servletOutputStream;
    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private InvoiceDto invoiceDto;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Use ReflectionTestUtils to inject mocked dependencies for Mockito 1.9.5
        ReflectionTestUtils.setField(invoiceDto, "invoiceFlow", invoiceFlow);
        ReflectionTestUtils.setField(invoiceDto, "orderFlow", orderFlow);
        ReflectionTestUtils.setField(invoiceDto, "invoiceClient", invoiceClient);
        ReflectionTestUtils.setField(invoiceDto, "orderApi", orderApi);
    }
    @Test
    public void testGenerateInvoice_Success() throws ApiException {
        // Arrange
        int orderId = 1;
        OrderPojo orderPojo = createOrderPojo(false); // Not invoiced
        InvoiceResponse mockResponse = createInvoiceResponse(123, "dummyBase64Pdf", "/tmp/invoice123.pdf");

        when(orderApi.getById(orderId)).thenReturn(orderPojo);
        when(invoiceFlow.generateInvoice(orderId)).thenReturn(mockResponse);
        doNothing().when(orderApi).updateOrder(any(OrderPojo.class));

        InvoiceResponse result = invoiceDto.generateInvoice(orderId);

        assertNotNull(result);
        assertEquals(Integer.valueOf(123), result.getInvoiceId());
        assertTrue(orderPojo.getIsInvoiced());
        assertEquals(OrderStatus.INVOICED, orderPojo.getStatus());
        
        verify(orderApi).getById(orderId);
        verify(invoiceFlow).generateInvoice(orderId);
        verify(orderApi).updateOrder(orderPojo);
    }

    @Test(expected = ApiException.class)
    public void testGenerateInvoice_OrderNotFound() throws ApiException {

        int orderId = 999;
        when(orderApi.getById(orderId)).thenReturn(null);

        invoiceDto.generateInvoice(orderId);
    }

    @Test(expected = ApiException.class)
    public void testGenerateInvoice_OrderAlreadyInvoiced() throws ApiException {
        int orderId = 1;
        OrderPojo orderPojo = createOrderPojo(true); // Already invoiced
        when(orderApi.getById(orderId)).thenReturn(orderPojo);
        invoiceDto.generateInvoice(orderId);
    }

    @Test
    public void testGenerateInvoice_InvoiceFlowThrowsException() throws ApiException {
        int orderId = 1;
        OrderPojo orderPojo = createOrderPojo(false);
        when(orderApi.getById(orderId)).thenReturn(orderPojo);
        when(invoiceFlow.generateInvoice(orderId)).thenThrow(new ApiException("Invoice generation failed"));

        try {
            invoiceDto.generateInvoice(orderId);
            fail("Should throw ApiException");
        } catch (ApiException e) {
            assertEquals("Invoice generation failed", e.getMessage());
        }
    }


    @Test
    public void testDownloadInvoice_Success() throws Exception {
        Integer orderId = 1;
        String base64Pdf = Base64.getEncoder().encodeToString("test pdf content".getBytes());
        InvoiceResponse mockResponse = new InvoiceResponse();
        mockResponse.setBase64Pdf(base64Pdf);
        mockResponse.setInvoiceId(orderId);

        when(invoiceClient.downloadInvoice(orderId)).thenReturn(mockResponse);
        when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);

        invoiceDto.downloadInvoice(orderId, httpServletResponse);

        verify(invoiceClient).downloadInvoice(orderId);
        verify(httpServletResponse).setContentType("application/pdf");
        verify(httpServletResponse).setHeader("Content-Disposition", "attachment; filename=myfile.pdf");
        verify(httpServletResponse).setContentLength(anyInt());
        verify(servletOutputStream).write(any(byte[].class));
        verify(servletOutputStream).flush();
    }

    @Test
    public void testDownloadInvoice_EmptyBase64() throws Exception {
        // Arrange
        Integer orderId = 1;
        InvoiceResponse mockResponse = new InvoiceResponse();
        mockResponse.setBase64Pdf("");
        mockResponse.setInvoiceId(orderId);

        when(invoiceClient.downloadInvoice(orderId)).thenReturn(mockResponse);
        when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
        invoiceDto.downloadInvoice(orderId, httpServletResponse);
        verify(invoiceClient).downloadInvoice(orderId);
        verify(httpServletResponse).setContentType("application/pdf");
    }


    @Test
    public void testGetInvoices_Success() throws ApiException {
        InvoiceFilterForm filterForm = createInvoiceFilterForm();
        List<InvoiceData> expectedInvoices = createInvoiceDataList();
        when(invoiceFlow.getInvoices(filterForm)).thenReturn(expectedInvoices);
        List<InvoiceData> result = invoiceDto.getInvoices(filterForm);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(1), result.get(0).getId());
        assertEquals(Integer.valueOf(2), result.get(1).getId());
        verify(invoiceFlow).getInvoices(filterForm);
    }

    @Test
    public void testGetInvoices_EmptyResult() throws ApiException {
        InvoiceFilterForm filterForm = createInvoiceFilterForm();
        List<InvoiceData> emptyList = new ArrayList<>();
        when(invoiceFlow.getInvoices(filterForm)).thenReturn(emptyList);
        List<InvoiceData> result = invoiceDto.getInvoices(filterForm);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(invoiceFlow).getInvoices(filterForm);
    }

    @Test(expected = ApiException.class)
    public void testGetInvoices_FlowThrowsException() throws ApiException {
        InvoiceFilterForm filterForm = createInvoiceFilterForm();
        when(invoiceFlow.getInvoices(filterForm)).thenThrow(new ApiException("Database error"));
        invoiceDto.getInvoices(filterForm);
    }

    @Test
    public void testGenerateInvoice_WithZeroOrderId() throws ApiException {
        int orderId = 0;
        when(orderApi.getById(orderId)).thenReturn(null);
        try {
            invoiceDto.generateInvoice(orderId);
            fail("Should throw ApiException for zero order ID");
        } catch (ApiException e) {
            assertTrue(e.getMessage().contains("Order id not found"));
        }
    }

    @Test
    public void testDownloadInvoice_WithZeroOrderId() throws Exception {
        Integer orderId = 0;
        String base64Pdf = Base64.getEncoder().encodeToString("test pdf content".getBytes());
        InvoiceResponse mockResponse = new InvoiceResponse();
        mockResponse.setBase64Pdf(base64Pdf);
        mockResponse.setInvoiceId(orderId);
        when(invoiceClient.downloadInvoice(orderId)).thenReturn(mockResponse);
        when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
        invoiceDto.downloadInvoice(orderId, httpServletResponse);
        verify(invoiceClient).downloadInvoice(orderId);
    }


}
