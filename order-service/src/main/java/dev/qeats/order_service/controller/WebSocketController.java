package dev.qeats.order_service.controller;

import dev.qeats.order_service.model.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/updates")
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send-to-user")
    public ResponseEntity<String> sendUpdateToUser(@RequestBody UpdateRequest updateRequest) {
        try {
            // Send message to a specific user
            messagingTemplate.convertAndSendToUser(
                    updateRequest.getUserId(), // User ID
                    "/queue/updates",         // Destination
                    updateRequest             // Payload
            );
            return ResponseEntity.ok("Update sent to user successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send update to user");
        }
    }


}
