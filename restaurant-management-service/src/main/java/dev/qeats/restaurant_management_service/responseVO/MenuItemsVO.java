package dev.qeats.restaurant_management_service.responseVO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemsVO {
    private Long id;

    private String name;

    private double price;

    private boolean isNonVeg;


    //    private Cuisine cuisine;
}
