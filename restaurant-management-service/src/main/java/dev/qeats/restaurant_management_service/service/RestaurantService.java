package dev.qeats.restaurant_management_service.service;

import dev.qeats.restaurant_management_service.controller.SortBy;
import dev.qeats.restaurant_management_service.model.Restaurant;
import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import dev.qeats.restaurant_management_service.responseVO.MenuItemsVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public interface RestaurantService {

    RestaurantVO updateRestaurantDetails(Long restaurantId, RestaurantVO restaurantVO);

    public void deleteRestaurant(Long restaurantId, String userId) throws Exception;

    RestaurantVO addRestaurant(RestaurantVO restaurantVO);

    RestaurantVO getRestaurant(Long restaurantId);

//    RestaurantVO getRestaurantByRestaurantName(String restaurantName);

//    List<RestaurantVO> getRestaurantsByLocation();

    public List<RestaurantVO> searchRestaurants(String city, String restaurantName, String cuisineName, SortBy sortBy, double userLat, double userLng);

    List<RestaurantVO> getRestaurants();

    List<MenuItemsVO> addMenuToRestaurant(long restaurantId, List<MenuItemsVO> menuItemsVO);

    void getUserRestaurant(String userId);

//    void getRestaurantByCuisine(String cuisine);

}
