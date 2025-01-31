package dev.qeats.order_service.model;

import dev.qeats.order_service.request.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    private String restaurantId;
    private String customerId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String paymentId;
    private String payerId;

    @Embedded
    private DeliveryAddress deliveryAddress;
    private String orderTime;
    private String lastUpdateTime;
    private String expectedDeliveryTime;
    private double shippingCharges;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    private double totalAmount;
}
