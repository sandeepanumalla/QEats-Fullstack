package dev.qeats.user_service.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponseVO {
    private long cartItemId;
    private String productId;
    private String productName;
    private String productDescription;
    private int quantity;
    private double price;
}
