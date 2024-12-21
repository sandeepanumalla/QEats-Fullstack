package dev.qeats.order_service.request;

import dev.qeats.order_service.model.OrderStatus;
import dev.qeats.order_service.request.DeliveryAddressVO;
import dev.qeats.order_service.request.OrderItemVO;
import dev.qeats.order_service.request.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderRequestVO {
    private String customerId;
    private String restaurantId;
    private String paymentId;
    private String payerId;
    private DeliveryAddressVO address; // Changed to use embedded address VO
    private OrderStatus orderStatus; // Changed to enum for consistency
    private double totalAmount;
    private PaymentStatus paymentStatus; // Introduced enum for payment status
    private List<OrderItemVO> items;
}