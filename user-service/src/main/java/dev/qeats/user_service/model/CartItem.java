package dev.qeats.user_service.model;

import dev.qeats.user_service.response.CartItemResponseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cart_items") // Map to the cart_items table
@Getter
@Setter
public class CartItem {

    @Id
    @Column("cart_item_id") // Explicit column mapping
    private long cartItemId;

    @Column("product_id")
    private String productId;

    @Column("product_name")
    private String productName;

    @Column("product_description")
    private String productDescription;

    @Column("quantity")
    private int quantity;

    @Column("price")
    private double price;

    @Column("cart_id") // Foreign key to Cart table
    private long cartId;

    public CartItem(String productId, String productName, String productDescription, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.price = price;
    }

    public CartItem() {
    }

    @Transient
    public CartItemResponseVO toCartItemResponseVO() {
        CartItemResponseVO cartItemResponseVO = new CartItemResponseVO();
        cartItemResponseVO.setProductId(this.productId);
        cartItemResponseVO.setProductName(this.productName);
        cartItemResponseVO.setProductDescription(this.productDescription);
        cartItemResponseVO.setQuantity(this.quantity);
        cartItemResponseVO.setPrice(this.price);
        return cartItemResponseVO;
    }
}
