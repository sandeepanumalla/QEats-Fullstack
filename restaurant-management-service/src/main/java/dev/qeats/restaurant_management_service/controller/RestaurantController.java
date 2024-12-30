package dev.qeats.restaurant_management_service.controller;

import dev.qeats.restaurant_management_service.model.Restaurant;
import dev.qeats.restaurant_management_service.responseVO.BranchVO;
import dev.qeats.restaurant_management_service.responseVO.RestaurantVO;
import dev.qeats.restaurant_management_service.service.BranchService;
import dev.qeats.restaurant_management_service.service.RestaurantService;
import dev.qeats.restaurant_management_service.service.impl.RestaurantServiceImpl;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static dev.qeats.restaurant_management_service.controller.SortBy.BEST_MATCH;


@RestController
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantService restaurantService;

    private final BranchService branchService;

    private final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:8003/realms/QEats/protocol/openid-connect/certs").build();

    public RestaurantController(RestaurantService restaurantService, BranchService branchService) {
        this.restaurantService = restaurantService;
        this.branchService = branchService;
    }

    @GetMapping("/restaurant")
    public ResponseEntity<List<RestaurantVO>> getRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false, defaultValue = "BEST_MATCH") SortBy sortBy, // Added sortBy parameter
            @RequestParam(required = false, defaultValue = "0.0") double userLat,
            @RequestParam(required = false, defaultValue = "0.0") double userLng  // Pass the user's location
    ) {
        List<RestaurantVO> restaurants = restaurantService.searchRestaurants(city, name, cuisine, sortBy, userLat, userLng); // Pass sortBy to the service layer

        if (restaurants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(restaurants);
    }

    @GetMapping("/restaurant/user/{userId}")
    public ResponseEntity<List<RestaurantVO>> getUserRestaurant(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(restaurantService.getUserRestaurant(userId));
    }

    // POST /api/restaurant
    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantVO> createRestaurant(@RequestBody RestaurantVO body) {
        RestaurantVO restaurantVO = restaurantService.addRestaurant(body);
        return ResponseEntity.ok().body(restaurantVO);
    }

    // PUT /api/restaurant/{restaurantId}
    @PutMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RestaurantVO> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantVO restaurantVO
    ) {
        RestaurantVO updatedRestaurant = restaurantService.updateRestaurantDetails(restaurantId, restaurantVO);
        return ResponseEntity.ok(updatedRestaurant);
    }

    // POST /restaurant/{restaurantId}/branch
    @PostMapping("/restaurant/{restaurantId}/branch")
    public ResponseEntity<RestaurantVO> addBranch(@PathVariable Long restaurantId, @RequestBody BranchVO branchVO) {
        RestaurantVO restaurant = branchService.addBranch(restaurantId, branchVO);
        return ResponseEntity.ok(restaurant);
    }

    // GEt /restaurant/{restaurantId}
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RestaurantVO> getRestaurant(@PathVariable Long restaurantId) {
        RestaurantVO restaurant = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    // DELETE /restaurant/{restaurantId}
    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long restaurantId, @CookieValue("JWT-TOKEN") String jwt) throws Exception {
        String userId = extractJwt(jwt).getClaims().get("sub").toString();
        restaurantService.deleteRestaurant(restaurantId, userId);
        return ResponseEntity.ok("Restaurant deleted");
    }

    // POST /restaurant/

    // webhook /restaurant/orders
    @PostMapping("/restaurant/orders")
    public ResponseEntity<String> updateOrders(@RequestBody Flux<Restaurant> body) {

        return ResponseEntity.ok("Orders updated");
    }

    private Jwt extractJwt(String token) throws JwtValidationException {
        Jwt jwt = jwtDecoder.decode(token); // Decode and validate JWT
        System.out.println("JWT is valid. Claims: " + jwt.getClaims());
        return jwt;
    }


}
