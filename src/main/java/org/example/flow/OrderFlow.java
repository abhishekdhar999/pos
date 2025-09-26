package org.example.flow;

import org.example.api.InventoryApi;
import org.example.api.OrderApi;
import org.example.api.OrderItemApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.enums.OrderStatus;
import org.example.models.data.ErrorData;
import org.example.models.data.OrderError;
import org.example.models.data.OrderItemData;
import org.example.models.form.OrderFiltersForm;
import org.example.pojo.InventoryPojo;
import org.example.pojo.OrderItemPojo;
import org.example.pojo.OrderPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.example.dto.DtoHelper.*;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class OrderFlow {
    @Autowired
    private ProductApi productApi;
    @Autowired
    private InventoryApi inventoryApi;
    @Autowired
    private OrderApi orderApi;
    @Autowired
    private OrderItemApi orderItemApi;

    public ErrorData<OrderError> create(List<OrderItemPojo> orderItemPojoList, List<String> barcodeList) {
        List<OrderError> orderErrorList = new ArrayList<>();
        OrderPojo order = new OrderPojo();
        order.setDateTime(ZonedDateTime.now(ZoneId.of("UTC")));
        order.setStatus(OrderStatus.CREATED);
        Integer orderId = orderApi.addOrder(order);
        int i = 0;
        Map<OrderItemPojo, InventoryPojo> validatedItems = new HashMap<>();
        List<OrderItemPojo> allItems = new ArrayList<>();
        for (OrderItemPojo item : orderItemPojoList) {
            try {
                ProductPojo product = productApi.getByBarcode(barcodeList.get(i));
                String barcode = barcodeList.get(i);
                validateProductInOrderCreation(product, barcode, item);
                item.setProductId(product.getId());
                InventoryPojo inventory = inventoryApi.getByProductId(product.getId());
                validateInventoryInOrderCreation(inventory, barcode, item);
                validatedItems.put(item, inventory);
                allItems.add(item);
            } catch (ApiException e) {
                OrderError error = createError(e.getMessage());
                orderErrorList.add(error);
                allItems.add(item);
            }
            i++;
        }
        addOrderItem(order, allItems);
        updateInventory(orderErrorList,validatedItems,order);
        return createErrorData(orderErrorList,orderId);
    }

        public void addOrderItem(OrderPojo order,List<OrderItemPojo> allItems ){
            for (OrderItemPojo item : allItems) {
                item.setOrderId(order.getId());
                orderItemApi.addOrderItem(item);
            }
        }

        public void updateInventory(List<OrderError> orderErrorList,Map<OrderItemPojo, InventoryPojo> validatedItems,OrderPojo order) {
            if (!orderErrorList.isEmpty()) {
                order.setStatus(OrderStatus.UNFULFILLABLE);
                orderApi.updateOrder(order);
            } else {
                for (Map.Entry<OrderItemPojo, InventoryPojo> entry : validatedItems.entrySet()) {
                    OrderItemPojo item = entry.getKey();
                    InventoryPojo inventory = entry.getValue();

                    inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
//                    inventoryApi.edit(inventory);
                }
                order.setStatus(OrderStatus.FULFILLABLE);
                orderApi.updateOrder(order);
            }
        }

    @Transactional
    public ErrorData<OrderError> resyncOrders(Integer id) throws ApiException {
        OrderPojo orderPojo = orderApi.getById(id);
        List<OrderError> errorList = new ArrayList<>();
        List<OrderItemPojo> orderItemPojoList = orderItemApi.getByOrderId(id);
        Map<Integer, InventoryPojo> inventoryMap = new HashMap<>();
        boolean allAvailable = true;
        for (int i = 0; i < orderItemPojoList.size(); i++) {
            OrderItemPojo orderItemPojo = orderItemPojoList.get(i);
            ProductPojo product = productApi.getById(orderItemPojo.getProductId());
            if (Objects.isNull(product)) {
                allAvailable = false;
            OrderError error = createError("product doesn't exist");
            errorList.add(error);
            } else {
                InventoryPojo inventory = inventoryApi.getByProductId(product.getId());
                inventoryMap.put(product.getId(), inventory);
                if (Objects.isNull(inventory)) {
                    allAvailable = false;
                    OrderError error = createError("No inventory found for product '" + product.getName() + "'");
                    errorList.add(error);
                } else if (inventory.getQuantity() < orderItemPojo.getQuantity()) {
                    allAvailable = false;
                    OrderError error = createError("Only " + inventory.getQuantity() + " left, but " + orderItemPojo.getQuantity() + " required");
                    errorList.add(error);
                }
            }
        }
        updateStatusWithInventory(allAvailable,orderPojo,orderItemPojoList,inventoryMap);
        return createErrorData(errorList,orderPojo.getId());
    }

    public void updateStatusWithInventory( boolean allAvailable,OrderPojo orderPojo,List<OrderItemPojo> orderItemPojoList, Map<Integer, InventoryPojo> inventoryMap) throws ApiException {
        if (allAvailable) {
            orderPojo.setStatus(OrderStatus.FULFILLABLE);
            orderApi.updateOrder(orderPojo);
            for (OrderItemPojo item : orderItemPojoList) {
                InventoryPojo inventory = inventoryMap.get(item.getProductId());
                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            }
        } else {
            orderPojo.setStatus(OrderStatus.UNFULFILLABLE);
            orderApi.updateOrder(orderPojo);
        }
    }

    public List<OrderItemPojo> getOrderItemsByOrderId(Integer orderId){
        return orderItemApi.getByOrderId(orderId);
    }
//    todo remove below methods move it to api
//    public List<OrderPojo> getAllOrders(OrderFiltersForm orderFiltersForm) throws ApiException{
//        return orderApi.getAllOrders(orderFiltersForm);
//    }

//    public OrderPojo getOrderById(Integer orderId) throws ApiException {
//        return orderApi.getById(orderId);
//    }

//    todo set name in one flow for order items
public List<OrderItemData> getOrderItemsByOrderIdWithProductName(List<OrderItemData> orderItemDataList) throws ApiException {
       for (OrderItemData orderItemData : orderItemDataList) {
           ProductPojo productPojo = productApi.getById(orderItemData.getProductId());
           if(Objects.isNull(productPojo)){
               throw new ApiException("Product not found");
           }
           orderItemData.setProductName(productPojo.getName());
       }
       return orderItemDataList;
}

    public ErrorData<OrderError> createErrorData(List<OrderError> orderErrorList,Integer orderId) {
        ErrorData<OrderError> result = new ErrorData<>();
        result.setErrorList(orderErrorList);
        result.setId(orderId);
        return result;
    }

}
