package dev.qeats.order_service.response;

import dev.qeats.order_service.model.DeliveryAddress;
import dev.qeats.order_service.request.DeliveryAddressVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderResponseVo {
    private long orderId;
    private String restaurantId;
    private String customerId;
    private String paymentStatus;
    private DeliveryAddressVO deliveryAddress;
    private String orderTime;
    private String expectedDeliveryTime;
    private double totalCost;
    private List<OrderItemResponseVO> items;
}
