//package dev.qeats.api_gateway.util;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Base64;
//
//@Service
//public class JwtService {
//
//    @Value("${jwt.secret-key}")
//    public String secretKey;
//
//    public Key key;
//
//    @PostConstruct
//    public void init() {
//        // Decode the base64 encoded secret key
//        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(decodedKey);
//    }
//
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setIssuer("auth-service")
//                .setIssuedAt(java.util.Date.from(java.time.Instant.now()))
//                .setExpiration(java.util.Date.from(java.time.Instant.now()
//                        .plusSeconds(3600)))
//                .setSubject(username)
//                .signWith(key).compact();
//    }
//
//    public String getUsername(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
