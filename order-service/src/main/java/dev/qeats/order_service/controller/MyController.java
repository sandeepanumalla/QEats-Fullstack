package dev.qeats.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qeats.order_service.request.OrderRequestVO;
import dev.qeats.order_service.response.OrderResponseVo;
import dev.qeats.order_service.service.OrderService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders")
public class MyController {

    private final OrderService orderService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:8003/realms/QEats/protocol/openid-connect/certs").build();


    @GetMapping("/dummy")
    public String getString(HttpServletRequest serverHttpRequest, HttpServletResponse serverHttpResponse) {
        return "hdummy";
    }

    @PostMapping("/process")
    public ResponseEntity<OrderResponseVo> processOrder(@RequestBody OrderRequestVO orderRequest) {
        log.info("Received order request: {}", orderRequest);

        try {
            // Process the order
            OrderResponseVo orderResponse = orderService.processOrder(orderRequest);
            // webhook to restaurant/orders
//            orderService.sendOrderToRestaurant(orderResponse);
            sendOrderUpdateMessage(orderResponse);
            log.info("Order processed successfully: {}", orderResponse);

            return ResponseEntity.ok(orderResponse);
        } catch (Exception ex) {
            log.error("Failed to process order: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // GET /order/restaurant/{restaurantId}
    @GetMapping
    public ResponseEntity<List<OrderResponseVo>> getOrdersByRestaurant(@CookieValue("JWT-TOKEN") String jwtToken) {
        try {
            Jwt jwt = jwtDecoder(jwtToken);
            String userId = jwt.getClaimAsString("sub");
            List<OrderResponseVo> orderResponse = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception ex) {
            log.error("Failed to get orders by restaurant: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    // PUT /order/{orderId}
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseVo> updateOrder(@PathVariable("orderId") long orderId, @RequestBody OrderRequestVO orderRequest) {
        try {
            OrderResponseVo orderResponse = orderService.updateOrder(orderId, orderRequest);

            return ResponseEntity.ok(orderResponse);
        } catch (Exception ex) {
            log.error("Failed to update order: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    public void sendOrderUpdateMessage(OrderResponseVo orderResponse) {
        try {
            // Extract user ID from the order response
            String userId = orderResponse.getCustomerId();

            // Log the message being sent
            log.info("Sending updated order to user: {}", userId);

            // Convert the order response object to a JSON string
            String orderUpdateMessage = new ObjectMapper().writeValueAsString(orderResponse);

            // Send the message to the user's WebSocket destination
            messagingTemplate.convertAndSendToUser(userId, "/queue/order-updates", orderUpdateMessage);

            log.info("Sent updated order message to user: {}", userId);
        } catch (Exception ex) {
            log.error("Error sending updated order message: {}", ex.getMessage());
        }
    }


    @GetMapping("/message")
    public ResponseEntity<String> sendMessage(@RequestParam("userId") String userId) {
        try {
            // Send a message to the specific user

            messagingTemplate.convertAndSendToUser(userId, "/queue/updates", new ObjectMapper().writeValueAsString("kaisa hai?"));
            System.out.println("Sent message to user: " + userId);

            // Return a success response
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception ex) {
            System.err.println("Error sending message: " + ex.getMessage());
            // Return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send message");
        }
    }

    private Jwt jwtDecoder(String jwtToken) {
        return  nimbusJwtDecoder.decode(jwtToken);
    }


    // Add the new API to fetch active orders for a given restaurant ID
    @GetMapping("/restaurants/active")
    public ResponseEntity<List<OrderResponseVo>> getActiveOrdersByRestaurants(
            @RequestParam List<Long> restaurantIds,
            HttpServletRequest request) {
        try {
            // Extract the JWT-TOKEN from cookies
            String jwtToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> "JWT-TOKEN".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElseThrow(() -> new RuntimeException("JWT-TOKEN not found in cookies"));

            // Fetch active orders using the service
            List<OrderResponseVo> activeOrders = orderService.getActiveOrdersByRestaurantIds(restaurantIds, jwtToken);

            // Return the active orders as the response
            return ResponseEntity.ok(activeOrders);
        } catch (Exception ex) {
            log.error("Failed to fetch active orders for restaurants {}: {}", restaurantIds, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}

