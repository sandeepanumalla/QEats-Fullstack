package dev.qeats.user_service.service.impl;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService {
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm("master") // Use the master realm for admin operations
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public void updateUserDetails(String userId, String firstName, String lastName) {
        Keycloak keycloak = getKeycloakInstance();

        try {
            // Retrieve the user representation
            UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();

            if (user != null) {
                // Update user details
                user.setFirstName(firstName);
                user.setLastName(lastName);

                // Perform the update
                keycloak.realm(realm).users().get(userId).update(user);

                // Log success
                System.out.println("User updated successfully");
            } else {
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            // Handle any exceptions
            throw new RuntimeException("Failed to update user details: " + e.getMessage(), e);
        }
    }

}
