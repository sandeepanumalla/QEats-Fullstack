package dev.qeats.auth_service.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
public class KeycloakAuthService {
    private static final String AUTH_ENDPOINT = "http://localhost:8003/realms/QEats/protocol/openid-connect/auth";
    private static final String CLIENT_ID = "qeats-app";
    private static final String REDIRECT_URI = "http://localhost:8765/auth/callback";
    private static final String SCOPE = "openid";
    private static final String RESPONSE_TYPE = "code";

    public void redirectToKeycloak(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        String keycloakAuthUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
                AUTH_ENDPOINT, CLIENT_ID, REDIRECT_URI, RESPONSE_TYPE, SCOPE);

        // Set the redirect status and location
        response.setStatus(HttpStatus.FOUND.value());
        response.sendRedirect(URI.create(keycloakAuthUrl).toString());
    }
}
