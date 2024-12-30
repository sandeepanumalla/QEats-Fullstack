package dev.qeats.order_service.response;

import dev.qeats.order_service.model.DeliveryAddress;
import dev.qeats.order_service.model.OrderStatus;
import dev.qeats.order_service.request.DeliveryAddressVO;
import dev.qeats.order_service.request.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderResponseVo {
    private long orderId;
    private String restaurantId;
    private CustomerDetailsVO customerDetails;
    private String customerId;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private DeliveryAddressVO deliveryAddress;
    private String orderTime;
    private String expectedDeliveryTime;
    private double totalCost;
    private List<OrderItemResponseVO> items;
}
