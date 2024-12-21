package dev.qeats.auth_service.service;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

//    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // Delegate to the default implementation for loading the user
//        OAuth2User oauth2User = delegate.loadUser(userRequest);
//
//        // Extract the OAuth2AccessToken and OAuth2RefreshToken
//        OAuth2AccessToken accessToken = userRequest.getAccessToken();
//        String refreshToken = userRequest.getAdditionalParameters().get("refresh_token").toString();
//
//        // Store or use the tokens as needed (e.g., storing them in a secure location)
//        System.out.println("Access Token: " + accessToken.getTokenValue());
//        System.out.println("Refresh Token: " + refreshToken);
//
//        // Return the OAuth2User to proceed with the authentication process
//        return new DefaultOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), "name");
//
//    }

    @Value("${jwt.secret-key}")  // Fetch the secret key from application.yml or properties
    private String secretKey;

    private JwtDecoder jwtDecoder = null;;

    private final JwtService jwtService;

    public CustomOauth2UserService(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    //    public CustomOAuth2UserService(JwtDecoder jwtDecoder) {
//        this.jwtDecoder = jwtDecoder;
//    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Extract the JWT token from the OAuth2 request
        String jwtToken = userRequest.getAccessToken().getTokenValue();
        byte[] keyBytes = secretKey.getBytes(); // Convert string key to byte array
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA256"); // Generate SecretKeySpec

        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(keySpec).build();
        // Decode the JWT token
//        Jwt decodedJwt = jwtDecoder.decode(jwtToken);

        // Extract relevant claims (like username, email, roles, etc.)
        String username = jwtService.getUsername(jwtToken);  // Example: username claim
        String email = jwtService.getEmail(jwtToken);  // Example: email claim
        Map<String, Object> claims = jwtService.extractClaims(jwtToken);

        // Check if the username is present, else fallback to another claim like "preferred_username"
        String userId = (username != null) ? username : (String) claims.get("preferred_username");

        // Create authorities if needed (example below shows roles extracted from claims)
        List<String> roles = (List<String>) claims.get("roles");
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // Ensure "userId" or equivalent is present
        if (userId == null) {
            throw new IllegalArgumentException("Missing attribute 'username' or 'preferred_username' in attributes");
        }

        // Manually build the OAuth2User object using the appropriate claim for the user identifier
        return new DefaultOAuth2User(authorities, claims, "sub");
    }
}
