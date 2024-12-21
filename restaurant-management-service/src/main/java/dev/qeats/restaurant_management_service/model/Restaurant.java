package dev.qeats.restaurant_management_service.model;


import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Restaurant extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId; // just added.
    private String name;
    private String description;
    private float rating;
    private double deliveryFee;
    private double deliveryTime;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuisine> cuisines;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems;

    public RestaurantVO toRestaurantVO() {
        RestaurantVO restaurantVO = new RestaurantVO();
        restaurantVO.setId(this.id);
        restaurantVO.setName(this.name);
        restaurantVO.setDescription(this.description);
        restaurantVO.setRating(this.rating);
        restaurantVO.setDeliveryFee(this.deliveryFee);
        restaurantVO.setDeliveryTime(this.deliveryTime);
        restaurantVO.setBranches(this.branches.stream().map(Branch::toBranchVO).toList());
        restaurantVO.setCuisines(this.cuisines.stream().map(Cuisine::toCuisineVO).toList());
        restaurantVO.setMenuItems(this.menuItems.stream().map(MenuItem::toMenuItemVO).toList());
        return restaurantVO;
    }

//    public List<BranchVO> convertBranchesToBranchVO() {
//        List<BranchVO> branchVOs = new ArrayList<>();
//        for (Branch branch : this.branches) {
//            branchVOs.add(branch.);
//        }
//    }

    public void addRating(double newRating) {
        if (this.rating == 0) {
            this.rating = (float) newRating;
            this.rating = 1;
            return;
        }
        this.rating += newRating;
        this.rating++;
        this.rating = this.rating / this.rating;
    }

}
