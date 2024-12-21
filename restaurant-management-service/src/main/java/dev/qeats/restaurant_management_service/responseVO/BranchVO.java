package dev.qeats.restaurant_management_service.responseVO;


import dev.qeats.restaurant_management_service.model.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BranchVO {
    private Long id;
    private String name;
    private AddressVO address;
//    private RestaurantVO restaurant;
//    private List<CuisineVO> cuisines;
//    private List<MenuItemsVO> menuItems;

}
