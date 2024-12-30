package dev.qeats.user_service.model;

import dev.qeats.user_service.response.CartItemResponseVO;
import dev.qeats.user_service.response.CartResponseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("carts") // Map to the carts table
@Getter
@Setter
public class Cart {

    @Id
    @Column("cart_id") // Explicit column mapping
    private long cartId;

    @Column("user_id") // Foreign key to the User table
    private String userId;

    @MappedCollection(idColumn = "cart_id") // Explicitly map the items relationship
    @Transient
    private List<CartItem> items;

    @Column("total_cost")
    private double totalCost = 0.0;



    @Transient
    public void setTotalCost() {
        this.totalCost = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
    }

    @Transient
    public CartResponseVO toCartResponseVO(List<CartItemResponseVO> cartItemResponseVO) {
        CartResponseVO cartResponseVO = new CartResponseVO();
        cartResponseVO.setUser(this.userId);
        cartResponseVO.setCartId(this.cartId);
        cartResponseVO.setItems(cartItemResponseVO);
        cartResponseVO.setTotalCost(this.totalCost);
        return cartResponseVO;
    }
}
