package dev.qeats.restaurant_management_service.Specification;

import dev.qeats.restaurant_management_service.model.Restaurant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class RestaurantSpecification {

    public static Specification<Restaurant> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) return null;
            Join<Object, Object> branch = root.join("branches", JoinType.INNER);
            return criteriaBuilder.equal(branch.get("address").get("city"), city);
        };
    }



    public static Specification<Restaurant> hasRestaurantName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Restaurant> hasCuisine(String cuisineName) {
        return (root, query, criteriaBuilder) -> {
            if (cuisineName == null || cuisineName.isEmpty()) return null;
            Join<Object, Object> cuisine = root.join("cuisines", JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(cuisine.get("name")), "%" + cuisineName.toLowerCase() + "%");
        };
    }


}
