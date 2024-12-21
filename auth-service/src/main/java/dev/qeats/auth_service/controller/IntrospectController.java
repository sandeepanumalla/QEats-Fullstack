package dev.qeats.auth_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/introspect")
@Slf4j
public class IntrospectController {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    private static final String INTROSPECT_ENDPOINT = "/protocol/openid-connect/token/introspect";

    @GetMapping
    public ResponseEntity<?> introspectToken(@CookieValue("ACCESS-TOKEN") String token) {
        // Prepare introspect URL
        String introspectUrl = keycloakIssuerUri + INTROSPECT_ENDPOINT;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret); // Use Basic Authentication with client credentials

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", token); // Add the token to be introspected

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<?> response = restTemplate.exchange(
                    introspectUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            log.info("Introspection Response: {}", response.getBody());

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Log the exception if something goes wrong
            log.error("Error during token introspection", e);
            return ResponseEntity.status(500).body(Map.of("error", "Introspection failed"));
        }
    }
}
