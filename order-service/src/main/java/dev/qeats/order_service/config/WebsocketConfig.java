package dev.qeats.order_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.qeats.order_service.interceptors.WebSocketAuthInterceptor;
import dev.qeats.order_service.interceptors.WebSocketAuthPreSendChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    private final WebSocketAuthPreSendChannelInterceptor webSocketAuthPreSendChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/orders/ws")

//                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOrigins("http://localhost:3000", "http://localhost:8675")
                .withSockJS();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(webSocketAuthPreSendChannelInterceptor);
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue"); // Use `/queue` for user-specific messages
        config.setApplicationDestinationPrefixes("/app"); // Prefix for client requests
        config.setUserDestinationPrefix("/user"); // Prefix for user-specific destinations
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
