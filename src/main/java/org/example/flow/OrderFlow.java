package org.example.flow;

import org.example.api.InventoryApi;
import org.example.api.OrderApi;
import org.example.api.OrderItemApi;
import org.example.api.ProductApi;
import org.example.dto.ApiException;
import org.example.enums.OrderStatus;
import org.example.models.data.ErrorData;
import org.example.models.data.OrderError;
import org.example.models.form.OrderFiltersForm;
import org.example.pojo.InventoryPojo;
import org.example.pojo.OrderItemPojo;
import org.example.pojo.OrderPojo;
import org.example.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // Step 1: Validate everything first
        int i = 0;
        Map<OrderItemPojo, InventoryPojo> validatedItems = new HashMap<>();
        List<OrderItemPojo> allItems = new ArrayList<>(); // Store all items for saving
        
        for (OrderItemPojo item : orderItemPojoList) {
            try {
                ProductPojo product = productApi.getByBarcode(barcodeList.get(i));
                if (product == null) {
                    throw new ApiException("Product with barcode '" + barcodeList.get(i) + "' does not exist");
                }
                if (product.getPrice() < item.getSellingPrice()) {
                    throw new ApiException("Selling Price is higher than MRP for product: " + product.getBarcode());
                }
                item.setProductId(product.getId());

                InventoryPojo inventory = inventoryApi.getByProductId(product.getId());
                if (inventory == null || inventory.getQuantity() <= 0) {
                    throw new ApiException("Product '" + product.getName() + "' with barcode '" + product.getBarcode() + "' is out of stock");
                }
                if (inventory.getQuantity() < item.getQuantity()) {
                    throw new ApiException("Only " + inventory.getQuantity() +
                            (inventory.getQuantity() == 1 ? " item is" : " items are") +
                            " left for product '" + product.getName() + "' with barcode '" + product.getBarcode() + "'");
                }

                validatedItems.put(item, inventory);
                allItems.add(item); // Add to all items

            } catch (ApiException e) {
                OrderError error = new OrderError();
                error.setBarcode(barcodeList.get(i));
                error.setIndex(i);
                error.setMessage(e.getMessage());
                orderErrorList.add(error);
                
                // Still add the item to allItems even if validation failed
                allItems.add(item);
            }
            i++;
        }

        // Step 2: Save all order items (both valid and invalid)
        for (OrderItemPojo item : allItems) {
            item.setOrderId(order.getId());
            orderItemApi.addOrderItem(item);
        }

        // Step 3: Decide status and reduce inventory
        if (!orderErrorList.isEmpty()) {
            order.setStatus(OrderStatus.UNFULFILLABLE);
            orderApi.updateOrder(order);
        } else {
            // All good → reduce inventory
            for (Map.Entry<OrderItemPojo, InventoryPojo> entry : validatedItems.entrySet()) {
                OrderItemPojo item = entry.getKey();
                InventoryPojo inventory = entry.getValue();

                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                inventoryApi.edit(inventory);
            }
            order.setStatus(OrderStatus.FULFILLABLE);
            orderApi.updateOrder(order);
        }

        ErrorData<OrderError> result = new ErrorData<>();
        result.setErrorList(orderErrorList);
        result.setId(orderId);
        return result;
    }



    @Transactional
    public ErrorData<OrderError> resyncOrders(Integer id) throws ApiException {
        OrderPojo orderPojo = orderApi.getById(id);
        List<OrderError> errorList = new ArrayList<>();
        List<OrderItemPojo> orderItemPojoList = orderItemApi.getByOrderId(id);

        boolean allAvailable = true;
        for (int i = 0; i < orderItemPojoList.size(); i++) {
            OrderItemPojo orderItemPojo = orderItemPojoList.get(i);
            ProductPojo product = productApi.getById(orderItemPojo.getProductId());

            if (product == null) {
                allAvailable = false;
                OrderError error = new OrderError();
                error.setIndex(i);
                error.setBarcode(null);
                error.setMessage("Product does not exist");
                errorList.add(error);
            } else {
                InventoryPojo inventory = inventoryApi.getByProductId(product.getId());
                if (inventory == null) {
                    allAvailable = false;
                    OrderError error = new OrderError();
                    error.setIndex(i);
                    error.setBarcode(product.getBarcode());
                    error.setMessage("No inventory found for product '" + product.getName() + "'");
                    errorList.add(error);
                } else if (inventory.getQuantity() < orderItemPojo.getQuantity()) {
                    allAvailable = false;
                    OrderError error = new OrderError();
                    error.setIndex(i);
                    error.setBarcode(product.getBarcode());
                    error.setMessage("Only " + inventory.getQuantity() + " left, but "
                            + orderItemPojo.getQuantity() + " required");
                    errorList.add(error);
                }
            }
        }

        if (allAvailable) {
            // Mark order as fulfillable (DO NOT reduce inventory during resync)
            orderPojo.setStatus(OrderStatus.FULFILLABLE);
            orderApi.updateOrder(orderPojo);
        } else {
            // Mark order as unfulfillable when inventory is insufficient
            orderPojo.setStatus(OrderStatus.UNFULFILLABLE);
            orderApi.updateOrder(orderPojo);
        }

        ErrorData<OrderError> result = new ErrorData<>();
        result.setId(orderPojo.getId());
        result.setErrorList(errorList);
        return result;
    }


    public List<OrderItemPojo> getAllOrderItem(){
        return orderItemApi.getAllOrderItems();
    }

    public List<OrderItemPojo> getOrderItemsByOrderId(Integer orderId){
        return orderItemApi.getByOrderId(orderId);
    }

    public List<OrderPojo> getAllOrders(OrderFiltersForm orderFiltersForm) throws ApiException{
        return orderApi.getAllOrders(orderFiltersForm);
    }

    public OrderPojo getOrderById(Integer orderId) throws ApiException {
        return orderApi.getById(orderId);
    }




}
