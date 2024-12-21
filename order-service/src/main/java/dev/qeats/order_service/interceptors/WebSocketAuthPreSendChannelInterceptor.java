package dev.qeats.order_service.interceptors;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthPreSendChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Extract the Authorization header
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String jwtToken = authHeader.substring(7); // Extract token after "Bearer "

        try {
            // Validate the JWT (implement your token validation logic here)
            validateJwtToken(jwtToken);

            // Optionally, add user information to the headers for downstream use
            accessor.setUser(new UsernamePasswordAuthenticationToken("userId", null));

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        return message;
    }

    private void validateJwtToken(String token) {
        // Implement your JWT validation logic here
        // Example: Decode and verify claims, expiration, signature, etc.
    }

    private String getUserIdFromToken(String token) {
        // Extract and return user ID from the JWT claims
        return "user123"; // Replace with actual extraction logic
    }
}
