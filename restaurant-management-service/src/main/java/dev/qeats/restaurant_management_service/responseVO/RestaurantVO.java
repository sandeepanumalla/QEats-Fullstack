package dev.qeats.restaurant_management_service.responseVO;

import dev.qeats.restaurant_management_service.model.Restaurant;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author : qeats
 * @date : 2021/11/17
 * @description :
 */

@Getter
@Setter
public class RestaurantVO {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    private float rating;
    @NotEmpty
    private double distance;
    private double deliveryFee;
    private double deliveryTime;
    private String userId;

    private List<BranchVO> branches;
    private List<CuisineVO> cuisines;

    private List<MenuItemsVO> menuItems;

    public Restaurant toRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setDescription(this.description);
        restaurant.setName(this.getName());
        restaurant.setRating(this.rating);
        restaurant.setDeliveryFee(this.deliveryFee);
        restaurant.setDeliveryTime(this.deliveryTime);
//        restaurant.setMenuItems();
        return  restaurant;
    }
}
