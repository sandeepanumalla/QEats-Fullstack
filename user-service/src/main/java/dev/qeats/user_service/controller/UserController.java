package dev.qeats.user_service.controller;

import dev.qeats.user_service.model.Address;
import dev.qeats.user_service.response.AddressVO;
import dev.qeats.user_service.response.UserProfileVO;
import dev.qeats.user_service.service.impl.CartServiceImpl;
import dev.qeats.user_service.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final CartServiceImpl cartService;
    private final UserServiceImpl userService;

    public UserController(CartServiceImpl cartService, UserServiceImpl userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

//    @GetMapping("/user/{userId}/cart")
//    public ResponseEntity<CartResponseVO> getUserCart(@PathVariable(name = "userId") long userId) throws Exception {
//        CartResponseVO cartResponseVO = cartService.getUserCart(userId);
//        return ResponseEntity.ok().body(cartResponseVO);
//    }

    // GET /api/user/{userId}/profile
    @GetMapping("/user/{userId}/profile")
    public Mono<ResponseEntity<UserProfileVO>> getUserProfile(@PathVariable(name = "userId") String userId, @CookieValue("JWT-TOKEN") String accessToken) {
        return userService.getUserProfile(userId, accessToken)
                .flatMap(userProfile -> Mono.just(ResponseEntity.ok().body(userProfile)));
    }

//    @GetMapping("/user/profile")
//    public Mono<ResponseEntity<KeycloakUserInfo>> getUser(ServerWebExchange serverWebExchange) {
//        return userService.userInfo(serverWebExchange);
//    }

    // PUT /api/user/{userId}/profile
//    @PutMapping("/user/{userId}/profile")
//    public ResponseEntity<UserProfileVO> updateUserProfile(
//            @PathVariable(name = "userId") long userId,
//            @RequestBody UserProfileVO userProfileVO) throws Exception {
//        UserProfileVO updatedProfile = userService.updateUserProfile(userId, userProfileVO);
//        return ResponseEntity.ok().body(updatedProfile);
//    }


    @PostMapping("/user/{userId}/address")
    public Mono<ResponseEntity<AddressVO>> getUserAddress(@PathVariable(name = "userId") String userId, @CookieValue("JWT-TOKEN") String accessToken, @RequestBody AddressVO addressVO) {
        return userService.addAddressToUser(userId, addressVO, accessToken).flatMap(addressVO1 -> Mono.just(ResponseEntity.ok(addressVO1)));
    }

    // GET /user/address
    @GetMapping("/user/{userId}/address")
    public Mono<ResponseEntity<List<Address>>> getUserAddresses(@PathVariable String userId) {
        return userService.getUserAddress(userId)
                .map(addresses -> ResponseEntity.ok().body(addresses))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{userId}/address")
    public ResponseEntity<String> updateUserAddresses(
            @PathVariable String userId,
            @RequestBody UserProfileVO userProfileVO) {
        if (!userId.equals(userProfileVO.getUserId())) {
            return ResponseEntity.badRequest().body("User ID mismatch");
        }

        userService.updateUserAddresses(userProfileVO);
        return ResponseEntity.ok("Addresses updated successfully");
    }

    @PutMapping("/user/{userId}/profile")
    public Mono<ResponseEntity<String>> updateUserProfile(@PathVariable String userId ,@Valid @RequestBody UserProfileVO userProfileVO) {
        return userService.updateUserProfile(userProfileVO)
                .then(Mono.just(ResponseEntity.ok("Profile updated successfully")));
    }


    // GET /api/user/${userId}/restaurant
//    @GetMapping("/user/{userId}/restaurant")
//    public Mono<ResponseEntity<List<String>>> getUserRestaurants(@PathVariable String userId) {
//        return userService.getUserRestaurants(userId)
//                .map(restaurants -> ResponseEntity.ok().body(restaurants))
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
}
