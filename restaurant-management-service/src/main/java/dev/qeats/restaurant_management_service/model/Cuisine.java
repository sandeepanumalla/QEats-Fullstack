package dev.qeats.restaurant_management_service.model;

import dev.qeats.restaurant_management_service.responseVO.CuisineVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Cuisine extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String imageUrl;

//    @ManyToOne
//    @JoinColumn(name = "branch_id")
//    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public CuisineVO toCuisineVO() {
        CuisineVO cuisineVO = new CuisineVO();
        cuisineVO.setId(this.id);
        cuisineVO.setName(this.name);
        cuisineVO.setDescription(this.description);
        cuisineVO.setImageUrl(this.imageUrl);
//        cuisineVO.setRestaurant(restaurantVO);
        return cuisineVO;
    }
}
