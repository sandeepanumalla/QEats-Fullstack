package dev.qeats.restaurant_management_service.controller;

import dev.qeats.restaurant_management_service.model.Restaurant;
import dev.qeats.restaurant_management_service.responseVO.CuisineVO;
import dev.qeats.restaurant_management_service.responseVO.MenuItemsVO;
import dev.qeats.restaurant_management_service.service.RestaurantService;
import dev.qeats.restaurant_management_service.service.impl.CuisineServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class CuisineController {

    private final CuisineServiceImpl cuisineService;
    private final RestaurantService restaurantService;

    public CuisineController(CuisineServiceImpl cuisineService, RestaurantService restaurantService) {
        this.cuisineService = cuisineService;
        this.restaurantService = restaurantService;
    }

    // POST /api/restaurant/{restaurantId}/cuisine
    @PostMapping("/restaurant/{restaurantId}/cuisine")
    public ResponseEntity<CuisineVO> addCuisineToRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody CuisineVO cuisineVO) {
        CuisineVO savedCuisine = cuisineService.addCuisineToRestaurant(restaurantId, cuisineVO);
        return ResponseEntity.ok(savedCuisine);
    }

    // PUT /api/restaurant/{restaurantId}/cuisine
    @PutMapping("/restaurant/{restaurantId}/cuisine/{cuisineId}")
    public ResponseEntity<CuisineVO> updateCuisineForRestaurant(
            @PathVariable Long restaurantId,
            @PathVariable Long cuisineId,
            @RequestBody CuisineVO cuisineVO) {
        CuisineVO updatedCuisine = cuisineService.updateCuisineForRestaurant(restaurantId, cuisineId, cuisineVO);
        return ResponseEntity.ok(updatedCuisine);
    }

    // GET /api/cuisines/filter
    // write api to fetch the filter cuisines from all restaurants
    @GetMapping("/cuisines/filter")
    public ResponseEntity<List<CuisineVO>> getFilterCuisines() {
        List<CuisineVO> cuisineVO = cuisineService.getAllCuisines();
        return ResponseEntity.ok(cuisineVO);
    }

    // POST /api/restaurant/{restaurantId}/menu
    // api for creating menu items
    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemsVO>> addMenuToRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody List<MenuItemsVO> menuItemsVO) {
        List<MenuItemsVO> menuItemsVOS = restaurantService.addMenuToRestaurant(restaurantId, menuItemsVO);
        return ResponseEntity.ok(menuItemsVOS);
    }
}
