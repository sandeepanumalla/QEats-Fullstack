package dev.qeats.restaurant_management_service.model;

import dev.qeats.restaurant_management_service.responseVO.MenuItemsVO;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private boolean isNonVeg;

    @ManyToOne
    private Cuisine cuisine;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    // create method for toMenuItemVO
    public MenuItemsVO toMenuItemVO() {
        MenuItemsVO menuItemsVO = new MenuItemsVO();
        menuItemsVO.setId(this.id);
        menuItemsVO.setName(this.name);
        menuItemsVO.setPrice(this.price);
        menuItemsVO.setNonVeg(this.isNonVeg);
        return menuItemsVO;
    }


}
