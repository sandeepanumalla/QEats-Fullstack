package dev.qeats.order_service.interceptors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpHeaders headers = request.getHeaders();
        List<String> cookies = headers.get(HttpHeaders.COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("JWT-TOKEN=")) {
                    String jwt = cookie.substring("JWT-TOKEN=".length());
                    if (validateJwt(jwt)) {
                        attributes.put("userId", getUserIdFromToken(jwt));
                        return true;
                    }
                }
            }
        }
        return false; // Reject handshake if no valid JWT is found
    }

    private boolean validateJwt(String jwt) {
        // Validate JWT logic
        return true; // Replace with actual validation
    }

    private String getUserIdFromToken(String jwt) {
        // Extract user ID from JWT logic
        return "user123"; // Replace with actual extraction
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
