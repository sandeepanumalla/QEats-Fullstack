package dev.qeats.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qeats.order_service.request.OrderRequestVO;
import dev.qeats.order_service.response.OrderResponseVo;
import dev.qeats.order_service.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/orders")
public class MyController {

    private final OrderService orderService;
    private final SimpMessagingTemplate messagingTemplate;

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

            log.info("Order processed successfully: {}", orderResponse);

            return ResponseEntity.ok(orderResponse);
        } catch (Exception ex) {
            log.error("Failed to process order: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // GET /order/restaurant/{restaurantId}
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<OrderResponseVo> getOrdersByRestaurant(@PathVariable("restaurantId") String restaurantId) {
        try {
            OrderResponseVo orderResponse = orderService.getOrdersByRestaurant(restaurantId);
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


}

