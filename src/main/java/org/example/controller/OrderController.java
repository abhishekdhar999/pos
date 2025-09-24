package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.dto.ApiException;
import org.example.dto.OrderDto;
import org.example.models.data.ErrorData;
import org.example.models.data.OrderData;
import org.example.models.data.OrderError;
import org.example.models.form.OrderFiltersForm;
import org.example.models.form.OrderItemForm;
import org.example.utils.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api
@RestController
@RequestMapping(value = "/api/order")
public class OrderController {
    @Autowired
    private OrderDto orderDto;

    @ApiOperation("create  order")
    @RequestMapping(method = RequestMethod.POST)
    public ErrorData<OrderError> create(@RequestBody List<OrderItemForm> orderItemFormList) throws ApiException {
        return orderDto.create(orderItemFormList);
    }

    @ApiOperation("getting an order details")
    @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
    public OrderData getOrderDetails(@PathVariable Integer orderId) throws ApiException{
        return orderDto.getOrderDetails(orderId);
    }

    @ApiOperation("getting all order's detail.")
    @RequestMapping(method = RequestMethod.GET)
    public PaginatedResponse<OrderData> getAll(@ModelAttribute OrderFiltersForm orderfilters) throws ApiException {
        System.out.println("order filter"+orderfilters.getStartDate()+" "+orderfilters.getEndDate()+" "+orderfilters.getPage()+" "+orderfilters.getSize());
        List<OrderData> data = orderDto.getAll(orderfilters);
        System.out.println("size"+data.size());
        PaginatedResponse<OrderData> response = new PaginatedResponse<>();
        response.setData(data);
        response.setPage(orderfilters.getPage());
        response.setSize(orderfilters.getSize());
        response.setTotalPages(getTotalCount(orderfilters) / orderfilters.getSize() + 1);
        return response;
    }

    @ApiOperation(value = "resync the order")
    @RequestMapping(value = "/resync/{id}",method = RequestMethod.POST)
    public ErrorData<OrderError> resync(@PathVariable Integer id) throws ApiException{
       return orderDto.resync(id);
    }

    @ApiOperation("getting total no. of orders")
    @RequestMapping(path = "/get-total-count", method = RequestMethod.GET)
    public Long getTotalCount(@ModelAttribute OrderFiltersForm orderFilters) throws ApiException {
        return orderDto.getTotalCount(orderFilters);
    }



}
