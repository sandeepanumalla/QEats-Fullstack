package dev.qeats.order_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String productId;
    private String productName;
    private String description;
    private int quantity;
    private double price;
    private String currency;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
