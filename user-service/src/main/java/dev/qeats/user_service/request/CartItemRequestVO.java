package dev.qeats.user_service.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemRequestVO {
    private long cartItemId;
    private String productId;
    private String productName;
    private String restaurantId;
    private String productDescription;
    private int quantity;
    private double price;
}

