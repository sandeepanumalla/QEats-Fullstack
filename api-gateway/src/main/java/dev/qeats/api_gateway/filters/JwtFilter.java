package dev.qeats.api_gateway.filters;

import com.nimbusds.jose.crypto.impl.HMAC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class JwtFilter implements WebFilter {

    private static final String COOKIE_NAME = "JWT-TOKEN";

    @Autowired
    private WebClient webClient;


    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUrl;

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/oauth2/authorization/keycloak",
            "/login/oauth2/code/keycloak",
        "/refresh-token",
        "/logout",
            "/login?logout",
            "/initiate-login"
    );

    // Nimbus JWT Decoder instance
    private final NimbusJwtDecoder jwtDecoder;

    public JwtFilter() {

        // Replace with your public key or secret
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:8003/realms/QEats/protocol/openid-connect/certs")
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().toString();

        // Skip validation for excluded paths
        if (EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith)) {
            log.info("Excluded path: {}. Skipping JWT validation.", requestPath);
            return chain.filter(exchange);
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        Optional<String> jwtFromCookie = extractJwtToken(exchange);

        // Case 1: Validate JWT from Authorization header and set JWT-TOKEN cookie
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.substring(7); // Extract token after "Bearer "
                try {
                    validateJwt(jwtToken);
                    log.info("Valid JWT from Authorization header. Setting JWT-TOKEN cookie.");
                    ResponseCookie cookie = generateResponseCookie(COOKIE_NAME, jwtToken);
                    exchange.getResponse().addCookie(cookie);
                    return chain.filter(exchange);
                } catch (JwtValidationException e) {
                    log.error("Invalid JWT token: {}. Refreshing token...", e.getMessage());
                    return handleTokenRefresh(exchange, chain);
                }
            }
        }

        // Case 2: Validate JWT from cookie and set Authorization header
        if (jwtFromCookie.isPresent()) {
            String jwtToken = jwtFromCookie.get();
            try {
                validateJwt(jwtToken);
                log.info("Valid JWT from cookie. Setting Authorization header.");
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (JwtValidationException e) {
                log.error("Invalid JWT token in cookie: {}. Refreshing token...", e.getMessage());
                return handleTokenRefresh(exchange, chain);
            }
        }

        // Case 3: Neither Authorization header nor JWT-TOKEN cookie is present
        log.error("No valid JWT found in request. Returning UNAUTHORIZED.");
        return setCorsHeadersAndReturnError(exchange, HttpStatus.UNAUTHORIZED);
    }

    private void validateJwt(String token) throws JwtValidationException {
        Jwt jwt = jwtDecoder.decode(token); // Decode and validate JWT
        System.out.println("JWT is valid. Claims: " + jwt.getClaims());
    }

    private Mono<Void> handleTokenRefresh(ServerWebExchange exchange, WebFilterChain chain) {
        String refreshToken = extractRefreshToken(exchange);

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.error("Missing or invalid refresh token. Returning UNAUTHORIZED.");
            return setCorsHeadersAndReturnError(exchange, HttpStatus.UNAUTHORIZED);
        }

        log.info("Attempting to refresh tokens with provided refresh token.");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId); // Replace with your client ID
        body.add("client_secret", clientSecret); // Replace with your client secret if required
        body.add("refresh_token", refreshToken);
        return webClient.post()
                .uri(tokenUrl)
                .body(BodyInserters.fromFormData(body))
//                .cookie("REFRESH-TOKEN", refreshToken)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String newAccessToken = (String) response.get("access_token");
                    String newRefreshToken = (String) response.get("refresh_token");

                    if (newAccessToken == null || newRefreshToken == null) {
                        log.error("Token refresh response is missing required fields. Returning UNAUTHORIZED.");
                        return setCorsHeadersAndReturnError(exchange, HttpStatus.UNAUTHORIZED);
                    }

                    log.info("Token refresh successful. Setting new tokens.");
                    exchange.getResponse().getCookies().set("JWT-TOKEN", generateResponseCookie("JWT-TOKEN", newAccessToken));
                    exchange.getResponse().getCookies().set("REFRESH-TOKEN", generateResponseCookie("REFRESH-TOKEN", newRefreshToken));

                    // Add the new access token to the Authorization header and proceed
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("Failed to refresh tokens: {}", e.getMessage());
                    return setCorsHeadersAndReturnError(exchange, HttpStatus.UNAUTHORIZED);
                });
    }

    private ResponseCookie generateResponseCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .build();
    }

    private String extractRefreshToken(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst("REFRESH-TOKEN"))
                .map(HttpCookie::getValue)
                .orElse(null);
    }


    private Optional<String> extractJwtToken(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest())
                .map(ServerHttpRequest::getCookies)
                .map(cookies -> cookies.getFirst(COOKIE_NAME))
                .map(cookie -> cookie.getValue());
    }

    private Mono<Void> setCorsHeadersAndReturnError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000"); // Replace with allowed origin
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
        return exchange.getResponse().setComplete();
    }
}
