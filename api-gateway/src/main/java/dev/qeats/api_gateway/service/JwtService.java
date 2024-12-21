package dev.qeats.api_gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    public String secretKey;

    public Key key;

//    List<String> excludeUrls = List.of("/login", "logout", );

    @PostConstruct
    public void init() {
        // Decode the base64 encoded secret key
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }
    public String extractUsername(String token) {
        return extractClaims(token).getSubject(); // The subject contains the username
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature");
        }
    }
    public String getEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);  // Assuming email is stored in the "email" claim
    }



    public String generateToken(Authentication authentication) {
        String username = null;

        // Check if the authentication is an instance of OAuth2AuthenticationToken
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = oauthToken.getPrincipal().getAttribute("preferred_username"); // Example: using an OAuth2 attribute
        } else {
            // For regular authentication (e.g., UsernamePasswordAuthenticationToken)
            username = authentication.getName(); // This will return the username
        }

        // Now use the extracted username to create the JWT token
        return Jwts.builder()
                .setIssuer("auth-service")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600))) // 1 hour expiration
                .setSubject(username) // Set the subject as the extracted username
                .setAudience("qeats") // Set the audience as "qeats"
                .claim("roles",
                        authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .signWith(key) // Assuming you have the secret key
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public OAuth2AccessToken getOAuth2AccessToken(String jwtToken) {
        // Parse the JWT to extract claims (optional)
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey)
                .build().parseClaimsJws(jwtToken).getBody();

        // Extract expiration and issue time
        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        // Create an OAuth2AccessToken using the JWT and claims
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,                // Token type (Bearer)
                jwtToken,                        // The JWT string itself as the access token
                Instant.ofEpochMilli(issuedAt.getTime()),     // Token issue time
                Instant.ofEpochMilli(expiration.getTime())    // Token expiration time
        );
    }
}
