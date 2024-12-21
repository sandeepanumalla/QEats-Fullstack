package dev.qeats.api_gateway.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

public class KeycloakAuthService  {
    private static final String AUTH_ENDPOINT = "http://localhost:8003/realms/QEats/protocol/openid-connect/auth";
    private static final String CLIENT_ID = "qeats-app";
    private static final String REDIRECT_URI = "http://localhost:8765/auth/callback";
    private static final String SCOPE = "openid";
    private static final String RESPONSE_TYPE = "code";

    public Mono<Void> redirectToKeycloak(ServerWebExchange exchange) {
        String keycloakAuthUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
                AUTH_ENDPOINT, CLIENT_ID, REDIRECT_URI, RESPONSE_TYPE, SCOPE);

        // Set the redirect location and status code
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create(keycloakAuthUrl));

        // Return an empty Mono since we are done with the response
        return exchange.getResponse().setComplete();
    }
}
