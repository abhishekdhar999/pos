package org.example.dto;

import org.example.api.OrderApi;
import org.example.flow.OrderFlow;
import org.example.models.data.ErrorData;
import org.example.models.data.OrderData;
import org.example.models.data.OrderError;
import org.example.models.data.OrderItemData;
import org.example.models.form.OrderFiltersForm;
import org.example.models.form.OrderItemForm;
import org.example.pojo.OrderItemPojo;
import org.example.pojo.OrderPojo;
import org.example.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.example.dto.DtoHelper.convertToOrderData;

@Component
public class OrderDto {
    @Autowired
    private OrderApi orderApi;

    @Autowired
    private OrderFlow orderFlow;

    public ErrorData<OrderError> create(List<OrderItemForm> orderItemFormList){
        List<OrderError> orderErrorList = UtilMethods.validateOrderFormList(orderItemFormList);
        List<OrderItemPojo> orderItemPojoList = DtoHelper.convertOrderFormListToOrderItemPojoList(orderItemFormList);

        List<String> barcodeList = new ArrayList<>();
        for (OrderItemForm form : orderItemFormList) {
            barcodeList.add(form.getBarcode());
        }
        if(!orderErrorList.isEmpty()){
            ErrorData<OrderError> errorData = new ErrorData<>();
            errorData.setErrorList(orderErrorList);
            return errorData;
        }
        return orderFlow.create(orderItemPojoList, barcodeList);
    }

    public OrderData getOrderDetails(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderFlow.getOrderById(orderId);
        List<OrderItemPojo> orderItemPojoList = orderFlow.getOrderItemsByOrderId(orderId);
        List<OrderItemData> orderItemDataList = DtoHelper.convertOrderItemPojoListToOrderItemDataList(orderItemPojoList);
        return convertToOrderData(orderPojo, orderItemDataList);
    }
    public List<OrderData> getAll(OrderFiltersForm orderFiltersForm) throws ApiException{
        List<OrderPojo> orderPojoList = orderFlow.getAllOrders(orderFiltersForm);
        List<OrderData> orderDataList = new ArrayList<>();
        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItemPojoList = orderFlow.getOrderItemsByOrderId(orderPojo.getId());
            List<OrderItemData> orderItemDataList = DtoHelper.convertOrderItemPojoListToOrderItemDataList(orderItemPojoList);
            orderDataList.add(convertToOrderData(orderPojo, orderItemDataList));
        }
        return orderDataList;
    }
    public Long getTotalCount(OrderFiltersForm orderFiltersForm) {
        return orderApi.getTotalCount(orderFiltersForm);
    }
    public ErrorData<OrderError> resync(Integer id) throws ApiException{
        return orderFlow.resyncOrders(id);
    }


}
