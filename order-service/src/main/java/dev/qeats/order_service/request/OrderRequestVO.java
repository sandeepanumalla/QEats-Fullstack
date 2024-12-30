package dev.qeats.order_service.request;

import dev.qeats.order_service.response.CustomerDetailsVO;
import dev.qeats.order_service.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderRequestVO {
    private String customerId;
    private CustomerDetailsVO customerDetails;
    private String restaurantId;
    private String paymentId;
    private String payerId;
    private DeliveryAddressVO deliveryAddress; // Changed to use embedded address VO
    private OrderStatus orderStatus; // Changed to enum for consistency
    private double totalAmount;
    private double shippingCharges;
    private List<OrderItemVO> items;
    private PaymentStatus paymentStatus; // Introduced enum for payment status
    //    private double handlingCharges;
}