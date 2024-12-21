//package dev.qeats.api_gateway.filters;
//
//import dev.qeats.api_gateway.service.KeycloakAuthService;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//public class SessionCheckFilter implements WebFilter {
//
//    private final KeycloakAuthService keycloakAuthService;
//
//    public SessionCheckFilter(KeycloakAuthService keycloakAuthService) {
//        this.keycloakAuthService = keycloakAuthService;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        // Check if the user is authenticated (this example assumes JWT is used for authentication)
//        return ReactiveSecurityContextHolder.getContext()
//                .flatMap(securityContext -> {
//                    // If there is an authenticated user, continue the filter chain
//                    if (securityContext.getAuthentication() != null && securityContext.getAuthentication().isAuthenticated()) {
//                        return chain.filter(exchange);
//                    }
//                    // If user is not authenticated, redirect to Keycloak
//                    return keycloakAuthService.redirectToKeycloak(exchange);
//                })
//                .switchIfEmpty(Mono.defer(() -> keycloakAuthService.redirectToKeycloak(exchange)));
//
//    }
//}
