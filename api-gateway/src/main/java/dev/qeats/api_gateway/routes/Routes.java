package dev.qeats.api_gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("order-service", r -> r.path("/orders/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.tokenRelay()
                                .removeRequestHeader("Origin"))
//                        .filters(f -> f.stripPrefix(1).tokenRelay())
                        .uri("lb://ORDER-SERVICE"))  // Load balances to ORDER-SERVICE
                .route("payment-service", r -> r.path("/payment/**")
//                        .filters(f -> f.stripPrefix(1).tokenRelay())
                        .uri("lb://PAYMENT-SERVICE"))  // Load balances to CART-SERVICE
                .route("user-service", r -> r.path("/api/user/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.tokenRelay()
                                .removeRequestHeader("Origin"))
                        .uri("lb://USER-SERVICE"))  // Load balances to USER-SERVICE
                .route("restaurant-service", r -> r.path("/api/restaurant/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.tokenRelay()
                                        .removeRequestHeader("Origin")
                        )
                        .uri("lb://RESTAURANT-MANAGEMENT-SERVICE"))  // Load balances to USER-SERVICE
                .route("backend-service", r -> r.path("/messages/**")
                        .filters(f -> f.tokenRelay())
                        .uri("http://localhost:8082"))
                .build();
    }

}
